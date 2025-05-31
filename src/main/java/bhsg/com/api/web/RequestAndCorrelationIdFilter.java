package bhsg.com.api.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static bhsg.com.api.logging.LoggingConstants.Symbols.*;
import static bhsg.com.api.logging.LoggingConstants.Tags.FILTER;

@Slf4j
@Component
public class RequestAndCorrelationIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String INITIALIZED_TRACE_CONTEXT = "Initialized trace context";
    public static final String CLEARING_TRACE_CONTEXT = "Clearing trace context";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElse(UUID.randomUUID().toString());

        String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElse(UUID.randomUUID().toString());

        MDC.put(REQUEST_ID_HEADER, requestId);
        MDC.put(CORRELATION_ID_HEADER, correlationId);

        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        if(log.isDebugEnabled()){
            log.debug("{} {} " + INITIALIZED_TRACE_CONTEXT + " requestId={}, correlationId={}, method={}, uri={}",
                    IN, FILTER, requestId, correlationId, request.getMethod(), request.getRequestURI());

        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            if(log.isDebugEnabled()){
                log.debug("{} {} {} " + CLEARING_TRACE_CONTEXT + " requestId={}, correlationId={}", OUT, FILTER, CLEANUP, requestId, correlationId);
            }
            MDC.remove(REQUEST_ID_HEADER);
            MDC.remove(CORRELATION_ID_HEADER);
        }
    }
}