package com.nomina_manager.nomina_api.tipos_deducciones;

import com.nomina_manager.tipos_deducciones.TiposDeducciones;
import com.nomina_manager.tipos_deducciones.TiposDeduccionesDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tipos-deducciones")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TiposDeduccionesController {
    private final TiposDeduccionesService service;

    @GetMapping
    public ResponseEntity<List<TiposDeducciones>> getTiposDeducciones() {
        return new ResponseEntity<>(service.encontrarTodosActivos(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TiposDeducciones> getTipoDeDeduccionPorId(@PathVariable Long id) {
        return new ResponseEntity<>(service.encontrarPorId(id), HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<TiposDeducciones> getTipoDeDeduccionPorNombre(@PathVariable String nombre) {
        return new ResponseEntity<>(service.encontrarPorNombre(nombre), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TiposDeducciones> crearTipoDeDeduccion(@RequestBody TiposDeduccionesDTO tipoDeduccion) {
        return new ResponseEntity<>(service.crearTipoDeDeduccion(tipoDeduccion), HttpStatus.OK);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<TiposDeducciones> modificarTipoDeDeduccion(@RequestBody TiposDeduccionesDTO tipoDeduccion) {
        return new ResponseEntity<>(service.modificarTipoDeDeduccion(tipoDeduccion), HttpStatus.OK);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTipoDeDeduccion(@PathVariable Long id) {
        return new ResponseEntity<>(service.eliminarTipoDeDeduccion(id), HttpStatus.OK);
    }
}
