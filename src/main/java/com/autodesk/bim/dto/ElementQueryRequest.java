package com.autodesk.bim.dto;

import lombok.Data;

@Data
public class ElementQueryRequest {
    private String elementType;
    private Long levelId;
    private String levelName;
    private String propertyName;
    private String propertyValue;
}
