package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ParseSummaryResponse {
    private Long modelId;
    private String modelName;
    private String schemaVersion;
    private int totalElements;
    private int totalLevels;
    private int totalProperties;
    private Map<String, Long> elementCountByType;
    private long parseTimeMs;
}
