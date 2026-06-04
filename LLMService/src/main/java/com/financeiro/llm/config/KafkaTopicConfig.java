package com.financeiro.llm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic topicExtratoAtualizado() {
        return TopicBuilder.name("extrato_atualizado")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicDlq() {
        return TopicBuilder.name("dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
