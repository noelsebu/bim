package com.autodesk.bim.kafka;

import com.autodesk.bim.domain.ParseJob;
import com.autodesk.bim.domain.ParseJobStatus;
import com.autodesk.bim.repository.ParseJobRepository;
import com.autodesk.bim.service.BimModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BimParseConsumer {

    private final ParseJobRepository jobRepository;
    private final BimModelService bimModelService;

    @KafkaListener(
            topics = KafkaTopicConfig.BIM_PARSE_REQUESTS,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(
            @Payload ParseJobMessage message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Processing parse job — jobId={}, partition={}, offset={}",
                message.getJobId(), partition, offset);

        ParseJob job = jobRepository.findById(message.getJobId()).orElse(null);
        if (job == null) {
            log.warn("Parse job not found in DB — jobId={}", message.getJobId());
            return;
        }

        job.setStatus(ParseJobStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        jobRepository.save(job);

        try (FileInputStream fis = new FileInputStream(message.getFilePath())) {
            var summary = bimModelService.parseFromStream(fis, message.getFileName());

            job.setStatus(ParseJobStatus.COMPLETED);
            job.setModelId(summary.getModelId());
            job.setTotalElements(summary.getTotalElements());
            job.setTotalLevels(summary.getTotalLevels());
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);

            log.info("Parse job COMPLETED — jobId={}, modelId={}, elements={}",
                    job.getId(), summary.getModelId(), summary.getTotalElements());

        } catch (IOException e) {
            markFailed(job, "Could not read temp file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Parse job FAILED — jobId={}", message.getJobId(), e);
            markFailed(job, e.getMessage());
        }
    }

    private void markFailed(ParseJob job, String reason) {
        job.setStatus(ParseJobStatus.FAILED);
        job.setErrorMessage(reason);
        job.setCompletedAt(LocalDateTime.now());
        jobRepository.save(job);
    }
}
