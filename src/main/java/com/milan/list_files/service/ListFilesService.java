package com.milan.list_files.service;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import com.milan.list_files.repository.ListFilesRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListFilesService {

    private final ListFilesRepository listFilesRepository;

    @Value("${folder.path}")
    private String directoryPath;

    @Value("${successful.folder.path}")
    private String successfulFolderPath;

    private Set<String> processedFiles = new HashSet<>();
    private int lastProcessedPage = 0;

    @Transactional
    @Scheduled(
            timeUnit = TimeUnit.MINUTES,
            initialDelayString = "${list.files.initialDelayMinutes}",
            fixedDelayString = "${list.files.fixedRateMinutes}")
    public void processFiles() {
        log.info("Start processing files");

        int pageSize = 10;

        Pageable pageable = PageRequest.of(lastProcessedPage, pageSize);

        Page<File> page = fetchFilesFromDirectory(directoryPath, pageable);

        if (!page.isEmpty()) {
            List<File> filesToProcess = page.getContent();
            filesToProcess.forEach(file -> {
                try {
                    if (file.isFile()) {
                        CallDetailRecordReport callDetailRecordReport = CallDetailRecordReport.builder()
                                .fileName(file.getName())
                                .reportStatusType(ReportStatusType.PENDING)
                                .build();
                        listFilesRepository.save(callDetailRecordReport);
                        processedFiles.add(file.getName());
                        moveFilesToSuccessfulFolder(file);
                        log.info("Imported file: {}", file.getName());
                    } else {
                        log.warn("Skipping directory: {}", file.getName());
                    }
                } catch (EntityExistsException e) {
                    log.warn("File {} already exists with PENDING status, skipping", file.getName());
                    log.error("Error message: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Error processing file {}: {}", file.getName(), e.getMessage());
                }
            });
            lastProcessedPage++;
        } else {
            log.warn("No files found in directory: {}", directoryPath);
            lastProcessedPage = 0;
        }

        log.info("Finished processing files");
    }

    private Page<File> fetchFilesFromDirectory(String directoryPath, Pageable pageable) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles(file -> file.isFile() && !new File(successfulFolderPath).equals(file.getParentFile()));

        List<File> fileList = files != null ? Arrays.asList(files) : Collections.emptyList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), fileList.size());

        if (start >= fileList.size() || start < 0 || end <= start) {
            return new PageImpl<>(Collections.emptyList(), pageable, fileList.size());
        }

        return new PageImpl<>(fileList.subList(start, end), pageable, fileList.size());
    }

    private void moveFilesToSuccessfulFolder(File file) {
        File successfulDirectory = new File(successfulFolderPath);
        if (!successfulDirectory.exists()) {
            successfulDirectory.mkdirs();
        }
        File destFile = new File(successfulDirectory, file.getName());
        if (file.renameTo(destFile)) {
            log.info("File {} moved to {}", file.getName(), successfulFolderPath);
        } else {
            log.error("Failed to move file {} to {}", file.getName(), successfulFolderPath);
        }
    }
}
