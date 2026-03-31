package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BimElementResponse {
    private Long id;
    private String ifcId;
    private String globalId;
    private String elementType;
    private String elementName;
    private String description;
    private String objectType;
    private String tag;
    private String levelName;
    private Long levelId;
    private Long modelId;
    private List<BimPropertyResponse> properties;
}
