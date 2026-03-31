package com.autodesk.bim.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bim_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BimLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ifc_id", nullable = false)
    private String ifcId;

    @Column(nullable = false)
    private String name;

    private Double elevation;

    @Column(name = "floor_height")
    private Double floorHeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private BimModel model;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BimElement> elements = new ArrayList<>();
}
