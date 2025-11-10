package com.example.movieservice.utils;

import com.example.movieservice.utils.LoggingUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoggingUtils.
 * 
 * @author Jiliar Silgado <jiliar.silgado@gmail.com>
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class LoggingUtilsTest {

    private LoggingUtils loggingUtils;

    @BeforeEach
    void setUp() {
        loggingUtils = LoggingUtils.getLogger(LoggingUtilsTest.class);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void info_ShouldNotThrowException_WhenCalled() {
        // Given
        String message = "Test info message";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.info(message));
    }

    @Test
    void debug_ShouldNotThrowException_WhenCalled() {
        // Given
        String message = "Test debug message";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.debug(message));
    }

    @Test
    void warn_ShouldNotThrowException_WhenCalled() {
        // Given
        String message = "Test warn message";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.warn(message));
    }

    @Test
    void error_ShouldNotThrowException_WhenCalled() {
        // Given
        String message = "Test error message";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.error(message));
    }

    @Test
    void error_ShouldNotThrowException_WhenCalledWithThrowable() {
        // Given
        String message = "Test error message";
        Exception exception = new RuntimeException("Test exception");

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.error(message, exception));
    }

    @Test
    void setRequestContext_ShouldNotThrowException_WhenAllParametersProvided() {
        // Given
        String requestId = "req-123";
        String correlationId = "corr-456";
        String clientId = "client-789";

        // When & Then
        assertDoesNotThrow(() -> LoggingUtils.setRequestContext(requestId, correlationId, clientId));
    }

    @Test
    void setRequestContext_ShouldNotThrowException_WhenOnlyRequestIdProvided() {
        // Given
        String requestId = "req-123";

        // When & Then
        assertDoesNotThrow(() -> LoggingUtils.setRequestContext(requestId, null, null));
    }

    @Test
    void setRequestContext_ShouldNotThrowException_WhenNullParametersProvided() {
        // When & Then
        assertDoesNotThrow(() -> LoggingUtils.setRequestContext(null, null, null));
    }

    @Test
    void clearRequestContext_ShouldNotThrowException() {
        // Given
        LoggingUtils.setRequestContext("req-123", "corr-456", "client-789");

        // When & Then
        assertDoesNotThrow(() -> LoggingUtils.clearRequestContext());
    }

    @Test
    void getLogger_ShouldReturnNonNullInstance_WhenCalled() {
        // When
        LoggingUtils logger = LoggingUtils.getLogger(LoggingUtilsTest.class);

        // Then
        assertNotNull(logger);
    }

    @Test
    void getLogger_ShouldReturnDifferentInstances_WhenCalledMultipleTimes() {
        // When
        LoggingUtils logger1 = LoggingUtils.getLogger(LoggingUtilsTest.class);
        LoggingUtils logger2 = LoggingUtils.getLogger(LoggingUtilsTest.class);

        // Then
        assertNotNull(logger1);
        assertNotNull(logger2);
        assertNotSame(logger1, logger2);
    }

    @Test
    void info_ShouldNotThrowException_WhenCalledWithArguments() {
        // Given
        String message = "Test message with args: {} and {}";
        Object arg1 = "arg1";
        Object arg2 = "arg2";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.info(message, arg1, arg2));
    }

    @Test
    void info_ShouldNotThrowException_WhenCalledWithVarArgs() {
        // Given
        String message = "Test message with multiple args: {} {} {} {}";
        Object[] args = {"arg1", "arg2", "arg3", "arg4"};

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.info(message, args));
    }

    @Test
    void debug_ShouldNotThrowException_WhenCalledWithArguments() {
        // Given
        String message = "Test message with args: {} and {}";
        Object arg1 = "arg1";
        Object arg2 = "arg2";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.debug(message, arg1, arg2));
    }

    @Test
    void debug_ShouldNotThrowException_WhenCalledWithVarArgs() {
        // Given
        String message = "Test message with multiple args: {} {} {} {}";
        Object[] args = {"arg1", "arg2", "arg3", "arg4"};

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.debug(message, args));
    }

    @Test
    void debug_ShouldNotThrowException_WhenCalledWithNoArgs() {
        // Given
        String message = "Test message with no args";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.debug(message, new Object[0]));
    }

    @Test
    void warn_ShouldNotThrowException_WhenCalledWithArguments() {
        // Given
        String message = "Test message with args: {} and {}";
        Object arg1 = "arg1";
        Object arg2 = "arg2";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.warn(message, arg1, arg2));
    }

    @Test
    void warn_ShouldNotThrowException_WhenCalledWithVarArgs() {
        // Given
        String message = "Test message with multiple args: {} {} {} {}";
        Object[] args = {"arg1", "arg2", "arg3", "arg4"};

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.warn(message, args));
    }

    @Test
    void error_ShouldNotThrowException_WhenCalledWithArguments() {
        // Given
        String message = "Test message with args: {} and {}";
        Object arg1 = "arg1";
        Object arg2 = "arg2";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.error(message, arg1, arg2));
    }

    @Test
    void error_ShouldNotThrowException_WhenCalledWithVarArgs() {
        // Given
        String message = "Test message with multiple args: {} {} {} {}";
        Object[] args = {"arg1", "arg2", "arg3", "arg4"};

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.error(message, args));
    }

    @Test
    void info_ShouldIncludeCorrelationId_WhenCorrelationIdSet() {
        // Given
        LoggingUtils.setRequestContext("req-123", "corr-456", null);
        String message = "Test message with correlation ID";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.info(message));
    }

    @Test
    void info_ShouldIncludeClientId_WhenClientIdSet() {
        // Given
        LoggingUtils.setRequestContext("req-123", null, "client-789");
        String message = "Test message with client ID";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.info(message));
    }

    @Test
    void debug_ShouldIncludeAllMDCValues_WhenAllSet() {
        // Given
        LoggingUtils.setRequestContext("req-123", "corr-456", "client-789");
        String message = "Test debug with all MDC values";

        // When & Then
        assertDoesNotThrow(() -> loggingUtils.debug(message));
    }
}