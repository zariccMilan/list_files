package com.milan.list_files;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@PropertySource("classpath:application-local.properties")
@EnableScheduling
public class ListFilesApplication {


    public static void main(String[] args) {
        SpringApplication.run(ListFilesApplication.class, args);


    }


}
