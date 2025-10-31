package com.lititi.exams;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class WebMainApplication {
    public static void main(String[] args) {
        String env = System.getenv("springProfileActive");
        String locationPath = "spring.config.location=optional:classpath:/" + env + "/";
        new SpringApplicationBuilder(WebMainApplication.class).properties(locationPath).build().run(args);
        System.out.println("启动成功!");
    }
}