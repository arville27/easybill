package net.arville.easybill.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@Slf4j
public class PerRequestHelper {
    private final Integer logId;
    private final Long startTime;
    private final ObjectMapper mapper;
    private final Random randomizer = new Random();

    public PerRequestHelper(Jackson2ObjectMapperBuilder objectMapperBuilder) {
        this.startTime = System.currentTimeMillis();
        this.logId = Math.abs(randomizer.nextInt(9_999_999));
        this.mapper = objectMapperBuilder.build();
    }

    public void log(LogLevel level, String message) {
        String logNumberSection = "(" + this.getLogId() + ")";
        switch (level) {
            case TRACE:
                log.trace(logNumberSection + " - {}", message);
                break;
            case DEBUG:
                log.debug(logNumberSection + " - {}", message);
                break;
            case INFO:
                log.info(logNumberSection + " - {}", message);
                break;
            case WARN:
                log.warn(logNumberSection + " - {}", message);
                break;
            case ERROR:
                log.error(logNumberSection + " - {}", message);
                break;
        }
    }

    public void log(LogLevel level, String messageWithSpecifier, Object... args) {
        String logNumberSection = "(" + this.getLogId() + ")";
        switch (level) {
            case TRACE:
                log.trace(logNumberSection + " - " + messageWithSpecifier, args);
                break;
            case DEBUG:
                log.debug(logNumberSection + " - " + messageWithSpecifier, args);
                break;
            case INFO:
                log.info(logNumberSection + " - " + messageWithSpecifier, args);
                break;
            case WARN:
                log.warn(logNumberSection + " - " + messageWithSpecifier, args);
                break;
            case ERROR:
                log.error(logNumberSection + " - " + messageWithSpecifier, args);
                break;
        }
    }

    public void logAndPrintException(String message, Throwable error) {
        String logMessage = "(" + this.getLogId() + ") - " + message;
        log.error(logMessage, error);
    }

    public String serialize(Object object) throws JsonProcessingException {
        return this.mapper.writeValueAsString(object);
    }

    public Long getCalculatedExecutionTime() {
        return System.currentTimeMillis() - this.startTime;
    }
}

