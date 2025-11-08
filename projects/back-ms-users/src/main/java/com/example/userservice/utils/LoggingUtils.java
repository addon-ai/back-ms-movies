package com.example.userservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the  microservice.
 * <p>
 * This utility provides consistent logging format across the application
 * with JSON format: {"microservice":"name","date":"yyyy-MM-dd","time":"HH:mm:ss.SSS","level":"LEVEL","class":"ClassName","message":"message"}
 * </p>
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
public class LoggingUtils {
    
    private static final String MICROSERVICE_NAME = "";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    private final Logger logger;
    private final String className;
    
    private LoggingUtils(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.className = clazz.getSimpleName();
    }
    
    public static LoggingUtils getLogger(Class<?> clazz) {
        return new LoggingUtils(clazz);
    }
    
    public void info(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage("INFO", message), args);
        }
    }
    
    public void debug(String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(formatMessage("DEBUG", message), args);
        }
    }
    
    public void warn(String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage("WARN", message), args);
        }
    }
    
    public void error(String message, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage("ERROR", message), args);
        }
    }
    
    public void error(String message, Throwable throwable, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage("ERROR", message), args, throwable);
        }
    }
    
    public static void setRequestContext(String requestId, String correlationId, String clientId) {
        if (requestId != null) {
            MDC.put("Request-ID", requestId);
        }
        if (correlationId != null) {
            MDC.put("Correlation-ID", correlationId);
        }
        if (clientId != null) {
            MDC.put("Client-Id", clientId);
        }
    }
    
    public static void clearRequestContext() {
        MDC.remove("Request-ID");
        MDC.remove("Correlation-ID");
        MDC.remove("Client-Id");
    }
    
    private String formatMessage(String level, String message) {
        LocalDateTime now = LocalDateTime.now();
        String requestId = MDC.get("Request-ID");
        String correlationId = MDC.get("Correlation-ID");
        String clientId = MDC.get("Client-Id");
        
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append(String.format("{\"microservice\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"level\":\"%s\",\"class\":\"%s\"",
                MICROSERVICE_NAME,
                now.format(DATE_FORMATTER),
                now.format(TIME_FORMATTER),
                level,
                className));
        
        if (requestId != null) {
            jsonBuilder.append(String.format(",\"Request-ID\":\"%s\"", requestId));
        }
        if (correlationId != null) {
            jsonBuilder.append(String.format(",\"Correlation-ID\":\"%s\"", correlationId));
        }
        if (clientId != null) {
            jsonBuilder.append(String.format(",\"Client-Id\":\"%s\"", clientId));
        }
        
        jsonBuilder.append(String.format(",\"message\":\"%s\"}", message));
        return jsonBuilder.toString();
    }
}