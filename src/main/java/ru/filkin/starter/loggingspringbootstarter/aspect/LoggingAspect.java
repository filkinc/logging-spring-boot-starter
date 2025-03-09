package ru.filkin.starter.loggingspringbootstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.filkin.starter.loggingspringbootstarter.properties.LoggingProperties;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    private final LoggingProperties loggingProperties;

    @Autowired
    public LoggingAspect(LoggingProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

//    @Pointcut("execution(* ru.filkin..*.*(..))")
//    public void allMethods() {
//    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {
    }

//    @Before("allMethods()")
//    public void logBeforeMethodsCall(JoinPoint joinPoint) {
//        if (loggingProperties.isEnabled()) {
//            String methodName = joinPoint.getSignature().getName();
//            Object[] args = joinPoint.getArgs();
//            log.info("Вызов метода {} с аргументами {}", methodName, args);
//        }
//    }
//
//    @AfterReturning(value = "allMethods()", returning = "result")
//    public void logAfterMethodCall(JoinPoint joinPoint, Object result) {
//        if (loggingProperties.isEnabled()) {
//            String methodName = joinPoint.getSignature().getName();
//            logAtLevel("Метод {} выполнен, результат {}", methodName, result);
//        }
//    }
//
//    @AfterThrowing(value = "allMethods()", throwing = "exception")
//    public void logMethodException(JoinPoint joinPoint, Exception exception) {
//        if (loggingProperties.isEnabled()) {
//            String methodName = joinPoint.getSignature().getName();
//            logAtLevel("Метод {} выбросил исключение {}", methodName, exception.getMessage());
//        }
//    }
//
//    @Around("allMethods()")
//    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//        if (loggingProperties.isEnabled()) {
//            String methodName = joinPoint.getSignature().getName();
//            long startTime = System.currentTimeMillis();
//            try {
//                Object result = joinPoint.proceed();
//                long resultTime = System.currentTimeMillis() - startTime;
//                logAtLevel("Метод {} выполнен за {} мс", methodName, resultTime);
//                return result;
//            } catch (Exception e) {
//                long resultTime = System.currentTimeMillis() - startTime;
//                logAtLevel("Метод {} выполнен за {} мс с исключением", methodName, resultTime);
//                throw e;
//            }
//        } else {
//            return joinPoint.proceed();
//        }
//    }


    @Before("restControllerMethods()")
    public void logBeforeRequest(JoinPoint joinPoint) {
        if (loggingProperties.isEnabled()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logAtLevel("HTTP Request: {} {}", request.getMethod(), request.getRequestURI());
        }
    }

    @AfterReturning(pointcut = "restControllerMethods()", returning = "response")
    public void logAfterResponse(JoinPoint joinPoint, Object response) {
        if (loggingProperties.isEnabled()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logAtLevel("HTTP Response: {} {} -> {}", request.getMethod(), request.getRequestURI(), response);
        }
    }


    public void logAtLevel(String massage, Object... args) {
        switch (loggingProperties.getLevel().toLowerCase()){
            case "debug":
                log.debug(massage, args);
                break;
            case "error":
                log.error(massage, args);
                break;
            case "warn":
                log.warn(massage, args);
                break;
            default:
                log.info(massage, args);
        }
    }


}
