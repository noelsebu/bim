package com.autodesk.bim.controller;

import com.autodesk.bim.dto.AsyncUploadResponse;
import com.autodesk.bim.dto.ParseJobResponse;
import com.autodesk.bim.service.ParseJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Async Parse Jobs", description = "Submit IFC files for async parsing and poll job status")
public class ParseJobController {

    private final ParseJobService parseJobService;

    @PostMapping(value = "/api/models/upload/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload IFC file for async parsing",
            description = "Accepts the file immediately and returns a jobId. " +
                          "The IFC is parsed in the background via Kafka. Poll /api/jobs/{jobId} for result."
    )
    public ResponseEntity<AsyncUploadResponse> submitAsync(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(parseJobService.submitParseJob(file));
    }

    @GetMapping("/api/jobs/{jobId}")
    @Operation(summary = "Get parse job status", description = "Returns QUEUED, PROCESSING, COMPLETED or FAILED")
    public ResponseEntity<ParseJobResponse> getJobStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(parseJobService.getJobStatus(jobId));
    }

    @GetMapping("/api/jobs")
    @Operation(summary = "List all parse jobs")
    public ResponseEntity<List<ParseJobResponse>> getAllJobs() {
        return ResponseEntity.ok(parseJobService.getAllJobs());
    }
}
