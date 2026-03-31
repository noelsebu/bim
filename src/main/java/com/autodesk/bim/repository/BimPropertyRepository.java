package com.autodesk.bim.repository;

import com.autodesk.bim.domain.BimProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BimPropertyRepository extends JpaRepository<BimProperty, Long> {

    List<BimProperty> findByElementId(Long elementId);

    List<BimProperty> findByElementIdAndPropertySet(Long elementId, String propertySet);

    @Query("SELECT DISTINCT p.propertyName FROM BimProperty p WHERE p.element.model.id = :modelId")
    List<String> findDistinctPropertyNamesByModelId(@Param("modelId") Long modelId);

    @Query("SELECT DISTINCT p.propertyValue FROM BimProperty p " +
           "WHERE p.element.model.id = :modelId AND p.propertyName = :propertyName")
    List<String> findDistinctValuesByModelIdAndPropertyName(
            @Param("modelId") Long modelId,
            @Param("propertyName") String propertyName);
}
