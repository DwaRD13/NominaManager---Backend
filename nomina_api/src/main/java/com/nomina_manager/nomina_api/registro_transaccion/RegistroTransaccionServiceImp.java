package com.nomina_manager.nomina_api.registro_transaccion;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.empleados.EmpleadoRepository;
import com.nomina_manager.nomina_api.tipos_deducciones.TiposDeduccionesRepository;
import com.nomina_manager.nomina_api.tipos_ingresos.TiposIngresosRepository;
import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import com.nomina_manager.registro_transaccion.RegistroTransaccionDTO;
import com.nomina_manager.tipos_deducciones.TiposDeducciones;
import com.nomina_manager.tipos_ingresos.TiposIngresos;
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

    private static final double PORCENTAJE_ARS = 0.0304;
    private static final double PORCENTAJE_AFP = 0.0287;

    private static final double SALARIO_MINIMO_PROMEDIO = 19352.50;
    private static final double TOPE_ARS = SALARIO_MINIMO_PROMEDIO * 10;
    private static final double TOPE_AFP = SALARIO_MINIMO_PROMEDIO * 20;

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
        validarDTO(dto);

        Empleado empleadoOrigen = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new DoNotExistException("Empleado no encontrado"));

        if (empleadoOrigen.getNombre().equalsIgnoreCase("Todos")) {
            List<Empleado> empleadosActivos = empleadoRepository.encontrarTodosActivosMenosGeneral();
            RegistroTransaccion ultima = null;

            for (Empleado emp : empleadosActivos) {
                ultima = procesarYGuardarTransaccion(emp, dto);
            }
            return ultima; // Retornamos la última o podrías retornar un resumen
        }

        return procesarYGuardarTransaccion(empleadoOrigen, dto);
    }

    @Override
    @Transactional
    public RegistroTransaccion modificarRegistroTransaccion(RegistroTransaccionDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID es requerido para modificar");
        }

        RegistroTransaccion entity = encontrarPorId(dto.getId());

        if (dto.getEmpleadoId() != null && !dto.getEmpleadoId().equals(entity.getEmpleado().getId())) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new DoNotExistException("Empleado no encontrado con ID: " + dto.getEmpleadoId()));
            entity.setEmpleado(empleado);
        }

        if (dto.getFecha() != null) {
            entity.setFecha(dto.getFecha());
        }

        boolean dependeDeSalario;
        Double porcentaje;

        if (dto.getTipoDeIngresoId() != null) {
            TiposIngresos tipoIngreso = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado con ID: " + dto.getTipoDeIngresoId()));
            entity.setTipoDeIngreso(tipoIngreso);
            entity.setTipoDeDeduccion(null);
            dependeDeSalario = tipoIngreso.isDependeDeSalario();
            porcentaje = tipoIngreso.getPorcentaje();
            entity.setTipoTransaccion(tipoIngreso.getNombre());
        } else if (dto.getTipoDeDeduccionId() != null) {
            TiposDeducciones tipoDeduccion = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                    .orElseThrow(() -> new DoNotExistException("Tipo de deduccion no encontrado con ID: " + dto.getTipoDeDeduccionId()));
            entity.setTipoDeDeduccion(tipoDeduccion);
            entity.setTipoDeIngreso(null);
            dependeDeSalario = tipoDeduccion.isDependeDeSalario();
            porcentaje = tipoDeduccion.getPorcentaje();
            entity.setTipoTransaccion(tipoDeduccion.getNombre());
        } else {
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
        }

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

            double salarioParaCalculo = empleado.getSalarioMensual().doubleValue();

            return BigDecimal.valueOf(salarioParaCalculo)
                    .multiply(BigDecimal.valueOf(porcentaje))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        if (montoRecibido == null) {
            throw new IllegalArgumentException("El monto es requerido cuando no depende del salario");
        }

        return montoRecibido;
    }

    private RegistroTransaccion procesarYGuardarTransaccion(Empleado empleado, RegistroTransaccionDTO dto) {
        RegistroTransaccion entity = new RegistroTransaccion();
        entity.setEmpleado(empleado);
        entity.setFecha(dto.getFecha());
        entity.setEstado(ESTADO_ACTIVO);
        entity.setFechaCreacion(LocalDateTime.now());

        if (dto.getTipoDeIngresoId() != null) {
            procesarIngreso(entity, empleado, dto);
        } else {
            procesarDeduccion(entity, empleado, dto);
        }

        return repository.save(entity);
    }

    private void procesarIngreso(RegistroTransaccion entity, Empleado empleado, RegistroTransaccionDTO dto) {
        TiposIngresos tipo = tiposIngresosRepository.findById(dto.getTipoDeIngresoId())
                .orElseThrow(() -> new DoNotExistException("Tipo de ingreso no encontrado"));

        entity.setTipoTransaccion(tipo.getNombre());
        entity.setTipoDeIngreso(tipo);

        if (tipo.getNombre().equalsIgnoreCase("salario completo") || tipo.getNombre().equalsIgnoreCase("Pago salario")) {
            double sueldoNeto = calcularDeduccionesYGenerarRegistros(empleado, dto.getFecha());
            entity.setMonto(BigDecimal.valueOf(sueldoNeto));
        } else {
            entity.setMonto(calcularMonto(dto.getMonto(), tipo.isDependeDeSalario(), tipo.getPorcentaje(), empleado));
        }
    }

    private void procesarDeduccion(RegistroTransaccion entity, Empleado empleado, RegistroTransaccionDTO dto) {
        TiposDeducciones tipo = tiposDeduccionesRepository.findById(dto.getTipoDeDeduccionId())
                .orElseThrow(() -> new DoNotExistException("Tipo de deducción no encontrado"));

        entity.setTipoTransaccion(tipo.getNombre());
        entity.setTipoDeDeduccion(tipo);

        entity.setMonto(calcularMonto(dto.getMonto(), tipo.isDependeDeSalario(), tipo.getPorcentaje(), empleado));
    }


    private Double calcularDeduccionesYGenerarRegistros(Empleado empleado, LocalDate fecha) {
        BigDecimal salarioBruto = empleado.getSalarioMensual();
        BigDecimal totalDeduccionesAcumuladas = BigDecimal.ZERO;

        List<TiposDeducciones> deduccionesDinamicas = tiposDeduccionesRepository
                .encontrarSiDependeDeSalarioYEsActivo();

        for (TiposDeducciones tipo : deduccionesDinamicas) {
            BigDecimal montoDeduccion = calcularMonto(null, true, tipo.getPorcentaje(), empleado);

            RegistroTransaccion deduccionEntity = new RegistroTransaccion();
            deduccionEntity.setEmpleado(empleado);
            deduccionEntity.setTipoTransaccion(tipo.getNombre());
            deduccionEntity.setTipoDeDeduccion(tipo);
            deduccionEntity.setMonto(montoDeduccion);
            deduccionEntity.setFecha(fecha);
            deduccionEntity.setEstado(ESTADO_ACTIVO);
            deduccionEntity.setFechaCreacion(LocalDateTime.now());

            repository.save(deduccionEntity);

            totalDeduccionesAcumuladas = totalDeduccionesAcumuladas.add(montoDeduccion);
        }

        return salarioBruto.subtract(totalDeduccionesAcumuladas).doubleValue();
    }

    private void registrarDeduccionAutomatica(Empleado emp, String nombreDeduccion, double monto, LocalDate fecha) {
        TiposDeducciones tipo = tiposDeduccionesRepository.buscarPorNombre(nombreDeduccion);

        RegistroTransaccion deduccion = new RegistroTransaccion();
        deduccion.setEmpleado(emp);
        deduccion.setTipoTransaccion(tipo.getNombre());
        deduccion.setTipoDeDeduccion(tipo);
        deduccion.setMonto(BigDecimal.valueOf(monto));
        deduccion.setFecha(fecha);
        deduccion.setEstado(ESTADO_ACTIVO);
        deduccion.setFechaCreacion(LocalDateTime.now());

        repository.save(deduccion);
    }

    private double calcularARS(double salarioBruto){
        double baseCalculoARS = Math.min(salarioBruto, TOPE_ARS);
        return baseCalculoARS * PORCENTAJE_ARS;
    }

    private double calcularAFP(double salarioBruto){
        double baseCalculoAFP = Math.min(salarioBruto, TOPE_AFP);
        return baseCalculoAFP * PORCENTAJE_AFP;
    }

    private void validarDTO(RegistroTransaccionDTO dto) {
        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }
        if (dto.getTipoDeIngresoId() == null && dto.getTipoDeDeduccionId() == null) {
            throw new IllegalArgumentException("Se debe proporcionar tipo de ingreso o deduccion");
        }
        if (dto.getTipoDeIngresoId() != null && dto.getTipoDeDeduccionId() != null) {
            throw new IllegalArgumentException("No se puede proporcionar ambos: tipo de ingreso y deduccion");
        }
    }

}