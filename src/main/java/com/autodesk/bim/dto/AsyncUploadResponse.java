package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsyncUploadResponse {
    private String jobId;
    private String status;
    private String message;
    private String statusUrl;
}
