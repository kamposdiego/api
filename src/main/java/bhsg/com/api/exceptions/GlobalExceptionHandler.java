package bhsg.com.api.exceptions;

import bhsg.com.api.logging.LoggingConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;

import java.util.*;
import java.util.stream.Collectors;

import static bhsg.com.api.exceptions.ErrorKeys.*;
import static bhsg.com.api.logging.LogContextUtils.trace;
import static bhsg.com.api.logging.LoggingConstants.Symbols.OUT;
import static bhsg.com.api.logging.LoggingConstants.Tags.CONTROLLER;
import static bhsg.com.api.web.RequestAndCorrelationIdFilter.CORRELATION_ID_HEADER;
import static bhsg.com.api.web.RequestAndCorrelationIdFilter.REQUEST_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MESSAGE_MISSING = "Something goes wrong. Please contact support with the trace ID.";

    private final MessageSource messageSource;

    private String localize(String code, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        return messageSource.getMessage(code, null, MESSAGE_MISSING,locale);
    }

    @ExceptionHandler(CacheServiceUnavailble.class)
    public ProblemDetail handleCacheServiceUnavailble(final CacheServiceUnavailble ex, final HttpServletRequest request) {
        log.error("{} {} {} {} - {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle(localize(CACHE_UNAVAILABLE.title(), request));
        problem.setDetail(localize(CACHE_UNAVAILABLE.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    // === Spring Data JPA Exceptions ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleConstraintViolation(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - Constraint validation failure: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(localize(METHOD_ARGUMENT_NOT_VALID.title(), request));

        problem.setDetail(localize(METHOD_ARGUMENT_NOT_VALID.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        List<Map<String, String>> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", Objects.requireNonNull(error.getDefaultMessage())))
                .collect(Collectors.toList());

        problem.setProperty("violations", violations);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("{} {} {} {} - Type mismatch: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(localize(METHOD_ARGUMENT_TYPE_MISMATCH.title(), request));

        problem.setDetail(String.format(localize(METHOD_ARGUMENT_TYPE_MISMATCH.detail(), request),
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown", ex.getValue()));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);

        String rootCause = Optional.ofNullable(ex.getCause())
                .map(Throwable::getMessage)
                .orElse("Type mismatch occurred.");
        problem.setProperty("errorCause", rootCause);

        log.debug("Type mismatch root cause: {}", rootCause);

        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(final DataIntegrityViolationException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - Constraint violation: {} - {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), ex.getMessage(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle(localize(DATA_INTEGRITY.title(), request));
        problem.setDetail(localize(DATA_INTEGRITY.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(final ConstraintViolationException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - Constraint validation failure: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(localize(CONSTRAINT_VIOLATION.title(), request));
        problem.setDetail(localize(CONSTRAINT_VIOLATION.detail(), request));

        List<String> violations = ex.getConstraintViolations()
                .stream()
                .map(v -> String.format("[%s] %s", v.getPropertyPath(), v.getMessage()))
                .toList();

        problem.setProperty("violations", violations);
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ProblemDetail handleInvalidDataAccess(final InvalidDataAccessApiUsageException ex, final HttpServletRequest request) {
        log.error("{} {} {} {} - Invalid data access: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle(localize(INVALID_DATA_ACCESS.title(), request));
        problem.setDetail(localize(INVALID_DATA_ACCESS.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ProblemDetail handleEmptyResult(final EmptyResultDataAccessException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - Empty result for data access: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(localize(EMPTY_DATA_ACCESS.title(), request));
        problem.setDetail(localize(EMPTY_DATA_ACCESS.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(final OptimisticLockingFailureException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - Optimistic locking failure: {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle(localize(OPTIMISTIC.title(), request));
        problem.setDetail(localize(OPTIMISTIC.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    // === Domain Exceptions ===
    @ExceptionHandler(IllegalIdempotentStateException.class)
    public ProblemDetail handleIllegalIdempotentStateException(final IllegalIdempotentStateException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle(localize(ILLEGAL_IDEMPOTENT_STATE.title(), request));
        problem.setDetail(localize(ILLEGAL_IDEMPOTENT_STATE.detail(), request).formatted(Optional.ofNullable(MDC.get(REQUEST_ID_HEADER)).orElse("N/A")));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(final NotFoundException ex, final HttpServletRequest request) {
        log.warn("{} {} {} {} - {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle(localize(NOT_FOUND.title(), request));
        problem.setDetail(localize(NOT_FOUND.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    // === Generic Exception Handler (Fallback) ===
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(final Exception ex, final HttpServletRequest request) {
        log.error("{} {} {} {} - {}", OUT, CONTROLLER, request.getMethod(), request.getRequestURI(), trace(), ex);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle(localize(INTERNAL.title(), request));
        problem.setDetail(localize(INTERNAL.detail(), request));
        problem.setInstance(URI.create(request.getRequestURI()));
        this.addTraceMetadata(problem);
        return problem;
    }

    // === Trace Metadata Helper ===
    private void addTraceMetadata(ProblemDetail problem) {
        problem.setProperty("traceId", Optional.ofNullable(MDC.get(REQUEST_ID_HEADER)).orElse("N/A"));
        problem.setProperty("correlationId", Optional.ofNullable(MDC.get(CORRELATION_ID_HEADER)).orElse("N/A"));
        problem.setProperty("trace", trace());
        problem.setProperty("tags", List.of(LoggingConstants.Tags.SERVICE, LoggingConstants.Tags.ERROR));
    }

}
