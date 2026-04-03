package com.autodesk.bim.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BimParseProducer {

    private final KafkaTemplate<String, ParseJobMessage> kafkaTemplate;

    public void sendParseRequest(ParseJobMessage message) {
        log.info("Queueing parse job — jobId={}, file={}", message.getJobId(), message.getFileName());

        CompletableFuture<SendResult<String, ParseJobMessage>> future =
                kafkaTemplate.send(KafkaTopicConfig.BIM_PARSE_REQUESTS, message.getJobId(), message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to queue parse job jobId={}: {}", message.getJobId(), ex.getMessage());
            } else {
                log.info("Parse job queued — jobId={}, partition={}, offset={}",
                        message.getJobId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
