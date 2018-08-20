package example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class AppConfig {

    @Bean
    ObjectMapper bookMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .indentOutput(true)
                .build();
    }
}