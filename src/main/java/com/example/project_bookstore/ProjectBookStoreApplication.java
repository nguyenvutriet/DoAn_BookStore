package com.example.project_bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.retry.annotation.EnableRetry;

@EnableScheduling
@EnableRetry

@SpringBootApplication
public class ProjectBookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBookStoreApplication.class, args);
    }

}
