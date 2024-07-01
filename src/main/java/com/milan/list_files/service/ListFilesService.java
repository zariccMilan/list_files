package com.milan.list_files.service;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import com.milan.list_files.repository.ListFilesRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        importFiles(pageSize);
        moveAndMarkPendingFiles(pageSize);
        log.info("Finished processing files");
    }

    @Transactional
    public void importFiles(int pageSize) {
        log.info("Importing files files");

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
    }

    @Transactional
    public void moveAndMarkPendingFiles(int pageSize) {
        log.info("Moving and updating pending files");

        Pageable pageable = PageRequest.of(0, pageSize);
        Page<CallDetailRecordReport> pendingFilesPage = listFilesRepository.findByReportStatusType(ReportStatusType.PENDING, pageable);
        List<CallDetailRecordReport> pendingFiles = pendingFilesPage.getContent();

        if(!pendingFiles.isEmpty()) {
            pendingFiles.forEach(report -> {
                File file = new File(directoryPath, report.getFileName());
                if (!file.exists() && file.isFile()) {
                    moveFileAndSetStatus(file);
                } else {
                    log.warn("File {} not found for moving", report.getFileName());
                }
            });
        } else {
            log.info("No pending files to move and update.");
        }
    }

    @Transactional
    public void moveFileAndSetStatus(File file) {
        log.info("Moving file {} to Successful folder and updating status", file.getName());
        moveFilesToSuccessfulFolder(file);
        updateFileStatusToSuccess(file.getName());
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

    @Transactional
    public void updateFileStatusToSuccess(String fileName) {
        log.info("Updating status to SUCCESS for file: {}", fileName);
        listFilesRepository.updateReportStatusByFileName(fileName, ReportStatusType.SUCCESS);
        log.info("Updated status to SUCCESS for file: {}", fileName);
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


}
