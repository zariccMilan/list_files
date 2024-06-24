package com.milan.list_files;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.File;


@SpringBootApplication
@PropertySource("classpath:application-local.properties")
public class ListFilesApplication {

    @Value("${folder.path}")
    private String directoryPath;

    public static void main(String[] args) {
        SpringApplication.run(ListFilesApplication.class, args);


    }

    @PostConstruct
    public void listFiles() {
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

}
