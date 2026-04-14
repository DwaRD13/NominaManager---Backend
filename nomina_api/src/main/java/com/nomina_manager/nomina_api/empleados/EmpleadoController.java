package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/empleado")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {

    public EmpleadoService service;

    @GetMapping
    public ResponseEntity<List<Empleado>> getEmpleados(){
        return new ResponseEntity<>(service.encontrarTodosActivos(), HttpStatus.OK);
    }

    @GetMapping("/less_todos")
    public ResponseEntity<List<Empleado>> getEmpleadosMenosTodos(){
        return new ResponseEntity<>(service.encontrarTodosActivosMenosTodos(), HttpStatus.OK);
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<Empleado> getEmpleadoPorId (@PathVariable Long id){
        return new ResponseEntity<>(service.encontrarPorId(id), HttpStatus.OK);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Empleado> getEmpleadoPorNombre (@PathVariable String nombre){
        return new ResponseEntity<>(service.encontrarPorNombre(nombre), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado (@RequestBody EmpleadoDTO empleado){
         return new ResponseEntity<>(service.crearEmpleado(empleado), HttpStatus.OK);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Empleado> actualizarEmpleado (@RequestBody EmpleadoDTO empleado){
        return new ResponseEntity<>(service.modificarEmpleado(empleado), HttpStatus.OK);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarEmpleado (@PathVariable Long id){
        return new ResponseEntity<>(service.eliminarEmpleado(id), HttpStatus.OK);
    }
}
