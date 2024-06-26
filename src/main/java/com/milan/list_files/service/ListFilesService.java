package com.milan.list_files.service;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import com.milan.list_files.repository.ListFilesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class ListFilesService {

    private final ListFilesRepository listFilesRepository;

    @Value("${folder.path}")
    private String directoryPath;

    @PostConstruct
    public void initialProcessFiles() {
        processFiles();
    }

    @Scheduled(fixedRate = 10000) // 10 sec
    public void scheduledProcessFiles() {
        processFiles();
    }

    public void processFiles() {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        System.out.println("Start processing files");

        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                CallDetailRecordReport record = CallDetailRecordReport.builder()
                        .fileName(file.getName())
                        .reportStatusType(ReportStatusType.PENDING)
                        .build();
                listFilesRepository.save(record);
            });

        }
    }


}
