package ru.filkin.starter.loggingspringbootstarter.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {

    private boolean enabled;

    private String level;
}
