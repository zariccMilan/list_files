package com.milan.list_files.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ReportStatusType {
    TYPE1("type1", "Type1", ""),
    TYPE2("type2", "Type2", ""),
    TYPE3("type3", "Type3", "");

    private final String value;
    private final String label;
    private final String code;

    ReportStatusType(String value, String label, String code) {
        this.value = value;
        this.label = label;
        this.code = code;
    }

    public static ReportStatusType getType(String type) {
        if (type == null) {
            return null;
        }
        return Stream.of(ReportStatusType.values()).filter(t -> t.value.equals(type)).findFirst().orElse(null);
    }
}
