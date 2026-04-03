package com.autodesk.bim.repository;

import com.autodesk.bim.domain.ParseJob;
import com.autodesk.bim.domain.ParseJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParseJobRepository extends JpaRepository<ParseJob, String> {

    List<ParseJob> findByStatus(ParseJobStatus status);

    List<ParseJob> findByFileNameOrderByCreatedAtDesc(String fileName);
}
