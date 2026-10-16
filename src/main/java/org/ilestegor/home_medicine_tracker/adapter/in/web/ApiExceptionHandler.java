package org.ilestegor.home_medicine_tracker.adapter.in.web;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.ilestegor.home_medicine_tracker.domain.exception.AuthenticationFailedException;
import org.ilestegor.home_medicine_tracker.domain.exception.BusinessRuleException;
import org.ilestegor.home_medicine_tracker.domain.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail onNotFound(NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail onAuthenticationFailed(AuthenticationFailedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail onBusinessRule(BusinessRuleException e) {
        log.atDebug()
                .setMessage("Отказ по бизнес-правилу")
                .addKeyValue("event", "api.business_rule_rejected")
                .addKeyValue("reason", e.getMessage())
                .log();
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail onOptimisticLock(OptimisticLockingFailureException e) {
        log.atWarn()
                .setMessage("Конкурентное изменение остатка")
                .addKeyValue("event", "api.optimistic_lock_conflict")
                .addKeyValue("exception", e.getClass().getSimpleName())
                .log();
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Запись изменилась параллельно — обнови данные и повтори операцию");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail onDataIntegrity(DataIntegrityViolationException e) {
        log.atWarn()
                .setMessage("Нарушение целостности данных")
                .addKeyValue("event", "api.data_integrity_violation")
                .addKeyValue("constraint", constraintName(e))
                .log();
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Операция нарушает ограничение целостности данных");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail onValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> fields.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        log.atDebug()
                .setMessage("Тело запроса не прошло валидацию")
                .addKeyValue("event", "api.validation_failed")
                .addKeyValue("fields", fields.keySet())
                .log();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Тело запроса не прошло валидацию");
        problem.setProperty("fields", fields);
        return problem;
    }

    private static String constraintName(Throwable e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation
                    && violation.getConstraintName() != null) {
                return violation.getConstraintName();
            }
        }
        return e.getClass().getSimpleName();
    }
}
