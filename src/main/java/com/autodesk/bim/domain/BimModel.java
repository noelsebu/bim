package com.autodesk.bim.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bim_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BimModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "schema_version")
    private String schemaVersion;

    private String description;

    @Column(name = "project_guid")
    private String projectGuid;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "total_elements")
    private Integer totalElements;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BimElement> elements = new ArrayList<>();

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BimLevel> levels = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
    }
}
