package com.lojatenis.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class DatabaseLogger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Around("execution(* com.lojatenis.repository.*.*(..))")
    public Object logDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // Log antes da execução
        log.info("🔍 [DATABASE] {} | Executando: {}.{} | Argumentos: {}",
                timestamp, className, methodName, formatArgs(args));

        long startTime = System.currentTimeMillis();

        try {
            // Executar a operação
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            // Log de sucesso
            log.info("✅ [DATABASE] {} | Sucesso: {}.{} | Tempo: {}ms | Resultado: {}",
                    timestamp, className, methodName, duration, formatResult(result));

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            // Log de erro
            log.error("❌ [DATABASE] {} | Erro: {}.{} | Tempo: {}ms | Erro: {}",
                    timestamp, className, methodName, duration, e.getMessage());

            throw e;
        }
    }

    @Around("execution(* com.lojatenis.service.*.*(..))")
    public Object logServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        log.debug("🔧 [SERVICE] {} | Iniciando: {}.{}", timestamp, className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.debug("✅ [SERVICE] {} | Concluído: {}.{}", timestamp, className, methodName);
            return result;
        } catch (Exception e) {
            log.error("❌ [SERVICE] {} | Erro em {}.{}: {}", timestamp, className, methodName, e.getMessage());
            throw e;
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    if (arg instanceof String) return "\"" + arg + "\"";
                    if (arg.getClass().getPackage() != null &&
                            arg.getClass().getPackage().getName().startsWith("com.lojatenis.domain")) {
                        return arg.getClass().getSimpleName() + "@" + System.identityHashCode(arg);
                    }
                    return arg.toString();
                })
                .reduce((a, b) -> a + ", " + b)
                .map(s -> "[" + s + "]")
                .orElse("[]");
    }

    private String formatResult(Object result) {
        if (result == null) return "null";

        if (result instanceof java.util.Collection) {
            return "Collection[" + ((java.util.Collection<?>) result).size() + " items]";
        }

        if (result instanceof org.springframework.data.domain.Page) {
            org.springframework.data.domain.Page<?> page = (org.springframework.data.domain.Page<?>) result;
            return "Page[" + page.getContent().size() + " items, " + page.getTotalElements() + " total]";
        }

        if (result.getClass().getPackage() != null &&
                result.getClass().getPackage().getName().startsWith("com.lojatenis.domain")) {
            return result.getClass().getSimpleName() + "@" + System.identityHashCode(result);
        }

        return result.toString();
    }
}
