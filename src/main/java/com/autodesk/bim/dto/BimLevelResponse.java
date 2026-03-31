package com.autodesk.bim.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BimLevelResponse {
    private Long id;
    private String ifcId;
    private String name;
    private Double elevation;
    private Double floorHeight;
    private Integer elementCount;
}
