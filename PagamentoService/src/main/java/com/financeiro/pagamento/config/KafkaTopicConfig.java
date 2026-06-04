package com.financeiro.pagamento.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic topicPagamentoCriado() {
        return TopicBuilder.name("pagamento_criado")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
