package com.lojatenis.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.lojatenis.repository.*.*(..))")
    public Object logDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("🔍 Executando operação de banco: {}.{} com argumentos: {}",
                className, methodName, args);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("✅ Operação {}.{} concluída em {}ms",
                className, methodName, (endTime - startTime));

        return result;
    }

    @Around("execution(* com.lojatenis.service.*.*(..))")
    public Object logServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("🔧 Executando service: {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.debug("✅ Service {}.{} executado com sucesso", className, methodName);
            return result;
        } catch (Exception e) {
            log.error("❌ Erro no service {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
