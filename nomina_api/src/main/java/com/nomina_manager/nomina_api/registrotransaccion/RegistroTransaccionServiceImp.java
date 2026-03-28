package com.nomina_manager.nomina_api.registrotransaccion;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.empleados.EmpleadoRepository;
import com.nomina_manager.nomina_api.tiposdeducciones.TiposDeduccionesRepository;
import com.nomina_manager.nomina_api.tiposingresos.TiposIngresosRepository;
import com.nomina_manager.registrotransaccion.RegistroTransaccion;
import com.nomina_manager.registrotransaccion.RegistroTransaccionDTO;
import com.nomina_manager.tiposdeducciones.TiposDeducciones;
import com.nomina_manager.tiposingresos.TiposIngresos;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RegistroTransaccionServiceImp implements RegistroTransaccionService {

    private final RegistroTransaccionRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final TiposIngresosRepository tiposIngresosRepository;
    private final TiposDeduccionesRepository tiposDeduccionesRepository;

    private static final String ESTADO_ACTIVO = "1";
    private static final String ESTADO_INACTIVO = "0";

    @Override
    public List<RegistroTransaccion> encontrarTodos() {
        return repository.findAll();
    }

    @Override
    public List<RegistroTransaccion> encontrarTodosActivos() {
        return repository.findAll().stream()
                .filter(rt -> ESTADO_ACTIVO.equals(rt.getEstado()))
                .toList();
    }

    @Override
    public RegistroTransaccion encontrarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DoNotExistException("Registro de transacción no encontrado con ID: " + id));
    }

    @Override
    public List<RegistroTransaccion> encontrarPorEmpleadoId(Long empleadoId) {
        return repository.findByEmpleadoIdAndEstado(empleadoId, ESTADO_ACTIVO);
    }

    @Override
    @Transactional
    public RegistroTransaccion crearRegistroTransaccion(RegistroTransaccionDTO dto) {
        // Validar que se proporcione empleado
        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }

        // Validar que se proporcione tipo de ingreso o deduccion
        if (dto.getTipoDeIngresoId() == null && dto.getTipoDeDeduccionId() == null) {
            throw new IllegalArgumentException("Se debe proporcionar tipo de ingreso o deduccion");
        }

        // Validar que no se proporcionen ambos
        if (dto.getTipoDeIngresoId() != null && dto.getTipoDeDeduccionId() != null) {
            throw new IllegalArgumentException("No se puede proporcionar ambos: tipo de ingreso y deduccion");
        }

        // Buscar el empleado
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new DoNotExistException("Empleado no encontrado con ID: " + dto.getEmpleadoId()));

        // Variables para el tipo seleccionado
        String tipoTransaccion;
        boolean dependeDeSalario;
        Double porcentaje;

        // Obtener datos del tipo de ingreso o deduccion
        if (dto.getTipoDeIngresoId() != null) {
            TiposIngresos tipoIngreso = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado con ID: " + dto.getTipoDeIngresoId()));
            
            tipoTransaccion = tipoIngreso.getNombre();
            dependeDeSalario = tipoIngreso.isDependeDeSalario();
            porcentaje = tipoIngreso.getPorcentaje();
        } else {
            TiposDeducciones tipoDeduccion = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de deduccion no encontrado con ID: " + dto.getTipoDeDeduccionId()));
            
            tipoTransaccion = tipoDeduccion.getNombre();
            dependeDeSalario = tipoDeduccion.isDependeDeSalario();
            porcentaje = tipoDeduccion.getPorcentaje();
        }

        // Calcular el monto si depende del salario
        BigDecimal montoCalculado = calcularMonto(dto.getMonto(), dependeDeSalario, porcentaje, empleado);

        // Crear la entidad
        RegistroTransaccion entity = new RegistroTransaccion();
        entity.setEmpleado(empleado);
        entity.setTipoTransaccion(tipoTransaccion);
        entity.setFecha(dto.getFecha());
        entity.setMonto(montoCalculado);
        entity.setEstado(ESTADO_ACTIVO);
        entity.setFechaCreacion(LocalDateTime.now());

        // Setear tipo de ingreso o deduccion
        if (dto.getTipoDeIngresoId() != null) {
            entity.setTipoDeIngreso(tiposIngresosRepository.findById(dto.getTipoDeIngresoId()).orElse(null));
        } else {
            entity.setTipoDeDeduccion(tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId()).orElse(null));
        }

        return repository.save(entity);
    }

    @Override
    @Transactional
    public RegistroTransaccion modificarRegistroTransaccion(RegistroTransaccionDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID es requerido para modificar");
        }

        RegistroTransaccion entity = encontrarPorId(dto.getId());

        // Si cambia el empleado
        if (dto.getEmpleadoId() != null && !dto.getEmpleadoId().equals(entity.getEmpleado().getId())) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new DoNotExistException("Empleado no encontrado con ID: " + dto.getEmpleadoId()));
            entity.setEmpleado(empleado);
        }

        // Si cambia la fecha
        if (dto.getFecha() != null) {
            entity.setFecha(dto.getFecha());
        }

        // Obtener datos actualizados del tipo
        boolean dependeDeSalario;
        Double porcentaje;
        
        if (entity.getTipoDeIngreso() != null) {
            dependeDeSalario = entity.getTipoDeIngreso().isDependeDeSalario();
            porcentaje = entity.getTipoDeIngreso().getPorcentaje();
        } else if (entity.getTipoDeDeduccion() != null) {
            dependeDeSalario = entity.getTipoDeDeduccion().isDependeDeSalario();
            porcentaje = entity.getTipoDeDeduccion().getPorcentaje();
        } else {
            dependeDeSalario = false;
            porcentaje = null;
        }

        // Recalcular monto
        BigDecimal montoCalculado = calcularMonto(dto.getMonto(), dependeDeSalario, porcentaje, entity.getEmpleado());
        entity.setMonto(montoCalculado);

        // Actualizar tipo si se proporciona
        if (dto.getTipoDeIngresoId() != null) {
            TiposIngresos tipoIngreso = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado con ID: " + dto.getTipoDeIngresoId()));
            entity.setTipoDeIngreso(tipoIngreso);
            entity.setTipoDeDeduccion(null);
            entity.setTipoTransaccion(tipoIngreso.getNombre());
        } else if (dto.getTipoDeDeduccionId() != null) {
            TiposDeducciones tipoDeduccion = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de deduccion no encontrado con ID: " + dto.getTipoDeDeduccionId()));
            entity.setTipoDeDeduccion(tipoDeduccion);
            entity.setTipoDeIngreso(null);
            entity.setTipoTransaccion(tipoDeduccion.getNombre());
        }

        return repository.save(entity);
    }

    @Override
    @Transactional
    public String eliminarRegistroTransaccion(Long id) {
        RegistroTransaccion entity = encontrarPorId(id);
        entity.setEstado(ESTADO_INACTIVO);
        repository.save(entity);
        return "Registro de transacción eliminado exitosamente";
    }

    /**
     * Calcula el monto de la transaccion.
     * Si el tipo de ingreso/deduccion depende del salario, calcula el monto como:
     * monto = (salario_mensual * porcentaje) / 100
     * Si no depende del salario, usa el monto enviado desde el frontend.
     */
    private BigDecimal calcularMonto(BigDecimal montoRecibido, boolean dependeDeSalario, Double porcentaje, Empleado empleado) {
        if (dependeDeSalario) {
            if (porcentaje == null) {
                throw new IllegalArgumentException("El tipo de transacción requiere un porcentaje配置ado");
            }
            // monto = (salario * porcentaje) / 100
            return empleado.getSalarioMensual()
                    .multiply(BigDecimal.valueOf(porcentaje))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        // Si no depende del salario, usar el monto enviado
        if (montoRecibido == null) {
            throw new IllegalArgumentException("El monto es requerido cuando no depende del salario");
        }
        
        return montoRecibido;
    }
}
