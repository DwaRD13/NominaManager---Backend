package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.AsientoContable;
import com.nomina_manager.asiento_contable.AsientoContableDTO;
import com.nomina_manager.asiento_contable.Moneda;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/asiento_contable")
@AllArgsConstructor
public class AsientoContableController {

    private AsientoContableService asientoContableService;

    @GetMapping
    public ResponseEntity<List<AsientoContable>> getAllAsientoContable() {
        return new ResponseEntity<>(asientoContableService.getAllAsientoContable(), HttpStatus.OK);
    }

    @GetMapping("/monedas")
    public ResponseEntity<List<Moneda>> getMonedas() {
        return new ResponseEntity<>(asientoContableService.getAllMoneda(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AsientoContable> crearAsientoContable(@RequestBody Moneda moneda,
                                                                @RequestParam LocalDate fechaInicio,
                                                                @RequestParam LocalDate fechaFin,
                                                                @RequestParam(name = "descripcion") String descripcion){
        return new ResponseEntity<>(asientoContableService.crearAsientoContable(moneda, fechaInicio, fechaFin, descripcion), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AsientoContableDTO> getAsientoContableDetailsById(@PathVariable Long id) {
        return new ResponseEntity<>(asientoContableService.getAsientoContableDetailsById(id), HttpStatus.OK);
    }

}
