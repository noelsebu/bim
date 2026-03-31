package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BimModelResponse {
    private Long id;
    private String name;
    private String fileName;
    private String schemaVersion;
    private String description;
    private String projectGuid;
    private String organizationName;
    private LocalDateTime uploadedAt;
    private Integer totalElements;
    private Integer totalLevels;
}
