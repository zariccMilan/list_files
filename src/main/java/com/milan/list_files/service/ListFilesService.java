package com.milan.list_files.service;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.repository.ListFilesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.io.File;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ListFilesService {

    private final ListFilesRepository listFilesRepository;

    @Value("${folder.path}")
    private String directoryPath;

    @PostConstruct
    public void processFiles() {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            List<File> fileList = Arrays.asList(files);
            fileList.forEach(file -> {
                CallDetailRecordReport record = CallDetailRecordReport.builder()
                        .fileName(file.getName())
                        .build();
                listFilesRepository.save(record);
            });

        }
    }


}
