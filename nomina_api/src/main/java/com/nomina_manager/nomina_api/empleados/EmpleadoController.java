package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/empleado")
@AllArgsConstructor
public class EmpleadoController {

    public EmpleadoService service;

    @GetMapping
    public ResponseEntity<List<Empleado>> getEmpleados(){
        return new ResponseEntity<>(service.encontrarTodos(), HttpStatus.OK);
    }
}
