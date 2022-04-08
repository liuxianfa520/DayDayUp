package com.pandora.storage.es;


import com.pandora.storage.es.exception.config.EnableExceptionHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableExceptionHandler
public class MeheEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeheEsApplication.class, args);
    }

}
