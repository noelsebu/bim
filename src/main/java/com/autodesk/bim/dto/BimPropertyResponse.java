package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BimPropertyResponse {
    private Long id;
    private String propertySet;
    private String propertyName;
    private String propertyValue;
    private String propertyType;
}
