package com.milan.list_files.repository;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListFilesRepository extends JpaRepository<CallDetailRecordReport, UUID> {

    boolean existsByFileNameAndReportStatusType(String fileName, ReportStatusType reportStatusType);

    Page<CallDetailRecordReport> findByReportStatusType(ReportStatusType reportStatusType, Pageable pageable);
}
