package com.company.meetingroom.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JacksonConfig {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            Map<Class<?>, com.fasterxml.jackson.databind.JsonSerializer<?>> serializers = new HashMap<>();
            serializers.put(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));

            Map<Class<?>, com.fasterxml.jackson.databind.JsonDeserializer<?>> deserializers = new HashMap<>();
            deserializers.put(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));

            builder.serializersByType(serializers)
                    .deserializersByType(deserializers);
        };
    }
}
