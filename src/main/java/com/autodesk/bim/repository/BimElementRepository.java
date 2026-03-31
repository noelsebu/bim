package com.autodesk.bim.repository;

import com.autodesk.bim.domain.BimElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BimElementRepository extends JpaRepository<BimElement, Long> {

    List<BimElement> findByModelIdAndElementType(Long modelId, String elementType);

    List<BimElement> findByModelId(Long modelId);

    List<BimElement> findByLevelId(Long levelId);

    List<BimElement> findByModelIdAndLevelId(Long modelId, Long levelId);

    List<BimElement> findByModelIdAndElementTypeAndLevelId(Long modelId, String elementType, Long levelId);

    Optional<BimElement> findByModelIdAndGlobalId(Long modelId, String globalId);

    @Query("SELECT DISTINCT e.elementType FROM BimElement e WHERE e.model.id = :modelId")
    List<String> findDistinctElementTypesByModelId(@Param("modelId") Long modelId);

    @Query("SELECT e FROM BimElement e JOIN e.properties p " +
           "WHERE e.model.id = :modelId AND p.propertyName = :propName AND p.propertyValue = :propValue")
    List<BimElement> findByModelIdAndProperty(
            @Param("modelId") Long modelId,
            @Param("propName") String propName,
            @Param("propValue") String propValue);

    long countByModelIdAndElementType(Long modelId, String elementType);
}
