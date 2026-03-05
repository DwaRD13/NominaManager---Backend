package com.nomina_manager.nomina_api.tiposingresos;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;
import com.nomina_manager.tiposingresos.TiposIngresos;
import com.nomina_manager.tiposingresos.TiposIngresosDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tipos-ingresos")
@AllArgsConstructor
public class TiposIngresosController {
    public TiposIngresosService service;

    @GetMapping
    public ResponseEntity<List<TiposIngresos>> getTiposIngresos(){
        return new ResponseEntity<>(service.encontrarTodosActivos(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TiposIngresos> getTipoDeIngresoPorId (@PathVariable Long id){
        return new ResponseEntity<>(service.encontrarPorId(id), HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<TiposIngresos> getTipoDeIngresoPorNombre (@PathVariable String nombre){
        return new ResponseEntity<>(service.encontrarPorNombre(nombre), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TiposIngresos> crearTipoDeIngreso (@RequestBody TiposIngresosDTO tipoIngreso){
        return new ResponseEntity<>(service.crearTipoDeIngreso(tipoIngreso), HttpStatus.OK);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<TiposIngresos> modificarTipoDeIngreso (@RequestBody TiposIngresosDTO tipoIngreso){
        return new ResponseEntity<>(service.modificarTipoDeIngreso(tipoIngreso), HttpStatus.OK);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTipoDeIngreso (@PathVariable Long id){
        return new ResponseEntity<>(service.eliminarTipoDeIngreso(id), HttpStatus.OK);
    }
}
