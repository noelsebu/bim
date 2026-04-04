package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParseJobResponse {
    private String jobId;
    private String fileName;
    private String status;
    private Long modelId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer totalElements;
    private Integer totalLevels;
}
