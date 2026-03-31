package com.autodesk.bim.repository;

import com.autodesk.bim.domain.BimLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BimLevelRepository extends JpaRepository<BimLevel, Long> {

    List<BimLevel> findByModelIdOrderByElevationAsc(Long modelId);

    Optional<BimLevel> findByModelIdAndName(Long modelId, String name);

    boolean existsByModelIdAndIfcId(Long modelId, String ifcId);
}
