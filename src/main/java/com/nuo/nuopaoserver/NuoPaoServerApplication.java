package com.nuo.nuopaoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NuoPaoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuoPaoServerApplication.class, args);
    }

}
