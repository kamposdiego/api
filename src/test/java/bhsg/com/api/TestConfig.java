package bhsg.com.api;

import bhsg.com.api.service.CacheServiceGrpc;
import bhsg.com.api.service.RDBMSService;
import bhsg.com.cache.IdempotentServiceGrpc;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RDBMSService rdbmsService() {
        return mock(RDBMSService.class);
    }

    @Bean
    @Primary
    public CacheServiceGrpc cacheServiceGrpc() {
        return mock(CacheServiceGrpc.class);
    }

    @Bean
    @Primary
    @Qualifier("idempotentServiceBlockingStub")
    public IdempotentServiceGrpc.IdempotentServiceBlockingStub idempotentServiceBlockingStub() {
        return mock(IdempotentServiceGrpc.IdempotentServiceBlockingStub.class);
    }

    @Bean
    @Primary
    public Retry cacheRetry() {
        return Retry.of("test-retry", RetryConfig.custom()
                .maxAttempts(1)
                .waitDuration(Duration.ofMillis(10))
                .retryExceptions(Exception.class)
                .build());
    }

}
