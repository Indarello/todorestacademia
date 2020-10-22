package com.academia.todorestapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class TodorestappApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodorestappApplication.class, args);
        //System.out.println(для копирования в тестировании);
    }
}
