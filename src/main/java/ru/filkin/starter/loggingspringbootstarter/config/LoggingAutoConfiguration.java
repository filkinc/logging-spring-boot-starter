package ru.filkin.starter.loggingspringbootstarter.config;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.filkin.starter.loggingspringbootstarter.aspect.LoggingAspect;
import ru.filkin.starter.loggingspringbootstarter.properties.LoggingProperties;

@AutoConfiguration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "logging.aspect", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration {

    private final LoggingProperties loggingProperties;

    public LoggingAutoConfiguration(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

    @Bean
    public LoggingAspect loggingAspect(LoggingProperties loggingProperties) {
        return new LoggingAspect(loggingProperties);
    }
}
