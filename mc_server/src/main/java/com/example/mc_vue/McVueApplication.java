package com.example.mc_vue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mc_vue.mapper")
public class McVueApplication {

    public static void main(String[] args) {
        SpringApplication.run(McVueApplication.class, args);
    }

}
