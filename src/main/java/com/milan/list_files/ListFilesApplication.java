package com.milan.list_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.sql.PreparedStatement;

@SpringBootApplication
public class ListFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListFilesApplication.class, args);

        String directoryPath = "/Users/milanzaric/Vezbanje(java)/ListFiles/src/main/resources/ZaMilana";
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        }

        PreparedStatement preparedStatement = null;


    }

}
