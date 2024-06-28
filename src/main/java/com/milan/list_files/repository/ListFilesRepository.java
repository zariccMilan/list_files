package com.milan.list_files.repository;


import com.milan.list_files.CallDetailRecordReport;
import com.milan.list_files.enums.ReportStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ListFilesRepository extends JpaRepository<CallDetailRecordReport, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE CallDetailRecordReport r SET r.reportStatusType = :status WHERE r.fileName = :fileName")
    void updateReportStatusByFileName(@Param("fileName") String fileName, @Param("status") ReportStatusType status);

}
