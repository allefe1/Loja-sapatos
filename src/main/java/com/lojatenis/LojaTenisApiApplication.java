package com.lojatenis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class LojaTenisApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LojaTenisApiApplication.class, args);
    }

}
