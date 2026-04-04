package com.autodesk.bim.service;

import com.autodesk.bim.domain.ParseJob;
import com.autodesk.bim.domain.ParseJobStatus;
import com.autodesk.bim.dto.AsyncUploadResponse;
import com.autodesk.bim.dto.ParseJobResponse;
import com.autodesk.bim.exception.BimException;
import com.autodesk.bim.exception.ResourceNotFoundException;
import com.autodesk.bim.kafka.BimParseProducer;
import com.autodesk.bim.kafka.ParseJobMessage;
import com.autodesk.bim.repository.ParseJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParseJobService {

    private final ParseJobRepository jobRepository;
    private final BimParseProducer producer;

    @Value("${bim.upload.temp-dir:/tmp/bim-uploads}")
    private String uploadTempDir;

    @Transactional
    public AsyncUploadResponse submitParseJob(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".ifc")) {
            throw new BimException("Only .ifc files are supported");
        }

        Path uploadDir = Paths.get(uploadTempDir);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new BimException("Could not create upload directory", e);
        }

        // Persist job first to get UUID
        ParseJob job = ParseJob.builder()
                .fileName(fileName)
                .filePath("pending")
                .status(ParseJobStatus.QUEUED)
                .build();
        job = jobRepository.save(job);

        // Save file to temp dir with jobId prefix to avoid name collisions
        Path filePath = uploadDir.resolve(job.getId() + "_" + fileName);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            jobRepository.delete(job);
            throw new BimException("Failed to store uploaded file", e);
        }

        job.setFilePath(filePath.toString());
        jobRepository.save(job);

        // Fire and forget to Kafka
        ParseJobMessage message = ParseJobMessage.builder()
                .jobId(job.getId())
                .fileName(fileName)
                .filePath(filePath.toString())
                .build();
        producer.sendParseRequest(message);

        log.info("Parse job submitted — jobId={}, file={}", job.getId(), fileName);

        return AsyncUploadResponse.builder()
                .jobId(job.getId())
                .status(ParseJobStatus.QUEUED.name())
                .message("IFC file accepted for processing. Poll the status URL for updates.")
                .statusUrl("/api/jobs/" + job.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public ParseJobResponse getJobStatus(String jobId) {
        ParseJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("ParseJob not found: " + jobId));
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public List<ParseJobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ParseJobResponse toResponse(ParseJob job) {
        return ParseJobResponse.builder()
                .jobId(job.getId())
                .fileName(job.getFileName())
                .status(job.getStatus().name())
                .modelId(job.getModelId())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .totalElements(job.getTotalElements())
                .totalLevels(job.getTotalLevels())
                .build();
    }
}
