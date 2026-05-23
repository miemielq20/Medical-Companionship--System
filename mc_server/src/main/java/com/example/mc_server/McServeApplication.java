package com.example.mc_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mc_server.mapper")
public class McServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(McServeApplication.class, args);
    }

}
