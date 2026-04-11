package com.nomina_manager.nomina_api.registro_transaccion;

import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import com.nomina_manager.registro_transaccion.RegistroTransaccionDTO;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/registros-transaccion")
@AllArgsConstructor
public class RegistroTransaccionController {

    private final RegistroTransaccionService service;

    @GetMapping
    public ResponseEntity<List<RegistroTransaccion>> getRegistrosTransaccion() {
        return new ResponseEntity<>(service.encontrarTodosActivos(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<RegistroTransaccion> getRegistroTransaccionPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.encontrarPorId(id), HttpStatus.OK);
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<RegistroTransaccion>> getRegistrosPorEmpleado(@PathVariable Long empleadoId) {
        return new ResponseEntity<>(service.encontrarPorEmpleadoId(empleadoId), HttpStatus.OK);
    }

    @GetMapping("/consulta")
    public ResponseEntity<List<RegistroTransaccion>> consultar(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String tipoTransaccion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return ResponseEntity.ok(
                service.consultar(empleadoId, tipoTransaccion, fechaInicio, fechaFin)
        );
    }

    @PostMapping
    public ResponseEntity<RegistroTransaccion> crearRegistroTransaccion(@RequestBody RegistroTransaccionDTO registroTransaccion) {
        return new ResponseEntity<>(service.crearRegistroTransaccion(registroTransaccion), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<RegistroTransaccion> modificarRegistroTransaccion(@RequestBody RegistroTransaccionDTO registroTransaccion) {
        return new ResponseEntity<>(service.modificarRegistroTransaccion(registroTransaccion), HttpStatus.OK);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarRegistroTransaccion(@PathVariable Long id) {
        return new ResponseEntity<>(service.eliminarRegistroTransaccion(id), HttpStatus.OK);
    }
}
