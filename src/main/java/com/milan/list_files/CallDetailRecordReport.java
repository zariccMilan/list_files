package com.milan.list_files;

import com.milan.list_files.enums.ReportStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "voip_call_detail_record_report")
public class CallDetailRecordReport {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "file_name", unique = true, nullable = false, columnDefinition = "varchar(120)")
    private String fileName;

    @Column(name = "path", columnDefinition = "varchar(120)")
    private String path;

    @Column(name = "size", columnDefinition = "integer")
    private Integer size;

    @Column(name = "type", columnDefinition = "varchar(120)")
    private String type;

    @Column(name = "extension", columnDefinition = "varchar(120)")
    private String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status_type", columnDefinition = "varchar(120)") // for future, it should be nullable = false
    private ReportStatusType reportStatusType;

}
