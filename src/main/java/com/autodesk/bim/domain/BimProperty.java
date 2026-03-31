package com.autodesk.bim.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bim_properties", indexes = {
        @Index(name = "idx_property_name", columnList = "property_name"),
        @Index(name = "idx_property_element", columnList = "element_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BimProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_set")
    private String propertySet;

    @Column(name = "property_name", nullable = false)
    private String propertyName;

    @Column(name = "property_value")
    private String propertyValue;

    @Column(name = "property_type")
    private String propertyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id", nullable = false)
    private BimElement element;
}
