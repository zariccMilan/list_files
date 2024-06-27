package com.milan.list_files.service;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import com.milan.list_files.repository.ListFilesRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ListFilesService {

    private final ListFilesRepository listFilesRepository;

    @Value("${folder.path}")
    private String directoryPath;

    @Transactional
    @Scheduled(
            timeUnit = TimeUnit.MINUTES,
            initialDelayString = "${list.files.initialDelayMinutes}",
            fixedDelayString = "${list.files.fixedRateMinutes}")
    public void processFiles() {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        System.out.println("Start processing files");

        if (files != null) {
            Arrays.stream(files)
                    .limit(10)
                    .forEach(file -> {
                        try {
                            CallDetailRecordReport record = CallDetailRecordReport.builder()
                                    .fileName(file.getName())
                                    .reportStatusType(ReportStatusType.PENDING)
                                    .build();
                            listFilesRepository.save(record);
                        } catch (EntityExistsException e) {
                            System.out.println("File already exists" + file.getName());
                            e.printStackTrace();
                        }

            });

        }
    }


}
