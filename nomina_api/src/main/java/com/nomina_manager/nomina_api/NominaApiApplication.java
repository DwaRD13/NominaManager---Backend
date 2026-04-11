package com.nomina_manager.nomina_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.nomina_manager",
        "com.nomina_manager.nomina_api"
})
public class NominaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NominaApiApplication.class, args);
    }

}
