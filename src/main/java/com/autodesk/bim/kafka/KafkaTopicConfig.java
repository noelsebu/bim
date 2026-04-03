package com.autodesk.bim.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BIM_PARSE_REQUESTS = "bim.parse.requests";
    public static final String BIM_PARSE_RESULTS  = "bim.parse.results";

    @Bean
    public NewTopic bimParseRequestsTopic() {
        return TopicBuilder.name(BIM_PARSE_REQUESTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bimParseResultsTopic() {
        return TopicBuilder.name(BIM_PARSE_RESULTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
