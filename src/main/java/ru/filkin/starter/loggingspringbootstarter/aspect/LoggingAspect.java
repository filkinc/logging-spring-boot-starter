package ru.filkin.starter.loggingspringbootstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.filkin.starter.loggingspringbootstarter.properties.LoggingProperties;

@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final LoggingProperties loggingProperties;

    @Autowired
    public LoggingAspect(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

    @Pointcut("@annotation(ru.filkin.starter.loggingspringbootstarter.annotation.CustomAnnotation)")
    public void allMethods() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {
    }

    @Before("allMethods()")
    public void logBeforeMethodsCall(JoinPoint joinPoint) {
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            logAtLevel("Вызов метода {} с аргументами {}", methodName, args);
    }

    @AfterReturning(value = "allMethods()", returning = "result")
    public void logAfterMethodCall(JoinPoint joinPoint, Object result) {
            String methodName = joinPoint.getSignature().getName();
            logAtLevel("Метод {} выполнен, результат {}", methodName, result);
    }

    @AfterThrowing(value = "allMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
            String methodName = joinPoint.getSignature().getName();
            logAtLevel("Метод {} выбросил исключение {}", methodName, exception.getMessage());
    }

    @Around("allMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
            String methodName = joinPoint.getSignature().getName();
            long startTime = System.currentTimeMillis();
            try {
                Object result = joinPoint.proceed();
                long resultTime = System.currentTimeMillis() - startTime;
                logAtLevel("Метод {} выполнен за {} мс", methodName, resultTime);
                return result;
            } catch (Exception e) {
                throw e;
            }
    }


    @Before("restControllerMethods()")
    public void logBeforeRequest(JoinPoint joinPoint) {
            logger.debug("Работает logBeforeRequest, log level: {}", loggingProperties.getLevel());
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logAtLevel("HTTP Request: {} {}", request.getMethod(), request.getRequestURI());
    }

    @AfterReturning(pointcut = "restControllerMethods()", returning = "response")
    public void logAfterResponse(JoinPoint joinPoint, Object response) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logAtLevel("HTTP Response: {} {} -> {}", request.getMethod(), request.getRequestURI(), response);
    }


    public void logAtLevel(String message, Object... args) {
        String level = loggingProperties.getLevel().toLowerCase();
        logger.debug("Logging at level: {}", level);
        switch (level) {
            case "info":
                logger.info(message, args);
                break;
            case "error":
                logger.error(message, args);
                break;
            case "warn":
                logger.warn(message, args);
                break;
            case "debug":
                logger.debug(message, args);
                break;
            default:
                logger.info(message, args);
        }
    }

}
