package com.autodesk.bim.repository;

import com.autodesk.bim.domain.BimModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BimModelRepository extends JpaRepository<BimModel, Long> {

    Optional<BimModel> findByFileName(String fileName);

    List<BimModel> findBySchemaVersion(String schemaVersion);

    @Query("SELECT m FROM BimModel m WHERE m.organizationName = :orgName ORDER BY m.uploadedAt DESC")
    List<BimModel> findByOrganization(String orgName);

    boolean existsByFileName(String fileName);
}
