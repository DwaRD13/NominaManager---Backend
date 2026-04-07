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
import java.time.LocalDate;
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
        return repository.encontrarTodosActivos();
    }

    // CORRECCIÓN 1: Se quitó el "List<>" del tipo de retorno. Ahora devuelve un solo objeto.
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
    public List<RegistroTransaccion> consultar(
            Long empleadoId,
            String tipoTransaccion,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return repository.consultarDinamico(
                empleadoId,
                tipoTransaccion,
                fechaInicio,
                fechaFin
        );
    }

    @Override
    @Transactional
    public RegistroTransaccion crearRegistroTransaccion(RegistroTransaccionDTO dto) {
        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }
        if (dto.getTipoDeIngresoId() == null && dto.getTipoDeDeduccionId() == null) {
            throw new IllegalArgumentException("Se debe proporcionar tipo de ingreso o deduccion");
        }
        if (dto.getTipoDeIngresoId() != null && dto.getTipoDeDeduccionId() != null) {
            throw new IllegalArgumentException("No se puede proporcionar ambos: tipo de ingreso y deduccion");
        }

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new DoNotExistException("Empleado no encontrado con ID: " + dto.getEmpleadoId()));

        RegistroTransaccion entity = new RegistroTransaccion();
        entity.setEmpleado(empleado);
        entity.setFecha(dto.getFecha());
        entity.setEstado(ESTADO_ACTIVO);
        entity.setFechaCreacion(LocalDateTime.now());

        boolean dependeDeSalario;
        Double porcentaje;

        if (dto.getTipoDeIngresoId() != null) {
            TiposIngresos tipoIngreso = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado con ID: " + dto.getTipoDeIngresoId()));

            entity.setTipoTransaccion("INGRESO");
            entity.setTipoDeIngreso(tipoIngreso);
            dependeDeSalario = tipoIngreso.isDependeDeSalario();
            porcentaje = tipoIngreso.getPorcentaje();
        } else {
            TiposDeducciones tipoDeduccion = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de deduccion no encontrado con ID: " + dto.getTipoDeDeduccionId()));

            entity.setTipoTransaccion("DEDUCCION");
            entity.setTipoDeDeduccion(tipoDeduccion);
            dependeDeSalario = tipoDeduccion.isDependeDeSalario();
            porcentaje = tipoDeduccion.getPorcentaje();
        }

        BigDecimal montoCalculado = calcularMonto(dto.getMonto(), dependeDeSalario, porcentaje, empleado);
        entity.setMonto(montoCalculado);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public RegistroTransaccion modificarRegistroTransaccion(RegistroTransaccionDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID es requerido para modificar");
        }

        // CORRECCIÓN 2: Como ya arreglamos encontrarPorId, esto ya no lanza error
        RegistroTransaccion entity = encontrarPorId(dto.getId());

        if (dto.getEmpleadoId() != null && !dto.getEmpleadoId().equals(entity.getEmpleado().getId())) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new DoNotExistException("Empleado no encontrado con ID: " + dto.getEmpleadoId()));
            entity.setEmpleado(empleado);
        }

        if (dto.getFecha() != null) {
            entity.setFecha(dto.getFecha());
        }

        // CORRECCIÓN 3: Arreglamos la lógica aquí también para que asigne "INGRESO" o "DEDUCCION"
        String tipoTransaccionTexto;
        boolean dependeDeSalario;
        Double porcentaje;

        if (dto.getTipoDeIngresoId() != null) {
            TiposIngresos tipoIngreso = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado con ID: " + dto.getTipoDeIngresoId()));
            entity.setTipoDeIngreso(tipoIngreso);
            entity.setTipoDeDeduccion(null);
            tipoTransaccionTexto = "INGRESO"; // Aseguramos el string correcto
            dependeDeSalario = tipoIngreso.isDependeDeSalario();
            porcentaje = tipoIngreso.getPorcentaje();
        } else if (dto.getTipoDeDeduccionId() != null) {
            TiposDeducciones tipoDeduccion = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de deduccion no encontrado con ID: " + dto.getTipoDeDeduccionId()));
            entity.setTipoDeDeduccion(tipoDeduccion);
            entity.setTipoDeIngreso(null);
            tipoTransaccionTexto = "DEDUCCION"; // Aseguramos el string correcto
            dependeDeSalario = tipoDeduccion.isDependeDeSalario();
            porcentaje = tipoDeduccion.getPorcentaje();
        } else {
            if (entity.getTipoDeIngreso() != null) {
                tipoTransaccionTexto = "INGRESO";
                dependeDeSalario = entity.getTipoDeIngreso().isDependeDeSalario();
                porcentaje = entity.getTipoDeIngreso().getPorcentaje();
            } else if (entity.getTipoDeDeduccion() != null) {
                tipoTransaccionTexto = "DEDUCCION";
                dependeDeSalario = entity.getTipoDeDeduccion().isDependeDeSalario();
                porcentaje = entity.getTipoDeDeduccion().getPorcentaje();
            } else {
                tipoTransaccionTexto = null;
                dependeDeSalario = false;
                porcentaje = null;
            }
        }

        entity.setTipoTransaccion(tipoTransaccionTexto);

        BigDecimal montoCalculado = calcularMonto(dto.getMonto(), dependeDeSalario, porcentaje, entity.getEmpleado());
        entity.setMonto(montoCalculado);

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

    private BigDecimal calcularMonto(BigDecimal montoRecibido, boolean dependeDeSalario, Double porcentaje, Empleado empleado) {
        if (dependeDeSalario) {
            if (porcentaje == null) {
                throw new IllegalArgumentException("El tipo de transacción requiere un porcentaje configurado");
            }
            return empleado.getSalarioMensual()
                    .multiply(BigDecimal.valueOf(porcentaje))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        if (montoRecibido == null) {
            throw new IllegalArgumentException("El monto es requerido cuando no depende del salario");
        }

        return montoRecibido;
    }
}