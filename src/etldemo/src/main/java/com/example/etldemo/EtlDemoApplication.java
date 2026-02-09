package com.example.etldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EtlDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(EtlDemoApplication.class, args);
    }
}
// SELECT COUNT(*) FROM SALES_RECORDS
