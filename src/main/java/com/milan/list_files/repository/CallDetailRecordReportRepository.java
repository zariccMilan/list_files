package com.milan.list_files.repository;

import com.milan.list_files.CallDetailRecordReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CallDetailRecordReportRepository extends JpaRepository<CallDetailRecordReport, UUID> {

}
