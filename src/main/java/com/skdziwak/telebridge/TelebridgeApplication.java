package com.skdziwak.telebridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class TelebridgeApplication {
    public static final String BASE_PACKAGE = "com.skdziwak.telebridge";

    public static void main(String[] args) {
        SpringApplication.run(TelebridgeApplication.class, args);
    }

}
