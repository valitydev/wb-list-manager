package com.rbkmoney.wb.list.manager.config;

import com.rbkmoney.wb.list.manager.serializer.EventDeserializer;
import dev.vality.damsel.wb_list.Event;
import dev.vality.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaConsumer<Event> testEventKafkaConsumer() {
        return new KafkaConsumer<>(bootstrapServers, new EventDeserializer());
    }
}
