package com.autodesk.bim.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseJobMessage {
    private String jobId;
    private String fileName;
    private String filePath;
}
