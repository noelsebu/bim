package com.autodesk.bim.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bim_elements", indexes = {
        @Index(name = "idx_element_type", columnList = "element_type"),
        @Index(name = "idx_element_guid", columnList = "global_id"),
        @Index(name = "idx_element_model", columnList = "model_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BimElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ifc_id", nullable = false)
    private String ifcId;

    @Column(name = "global_id")
    private String globalId;

    @Column(name = "element_type", nullable = false)
    private String elementType;

    @Column(name = "element_name")
    private String elementName;

    private String description;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "tag")
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private BimModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private BimLevel level;

    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BimProperty> properties = new ArrayList<>();
}
