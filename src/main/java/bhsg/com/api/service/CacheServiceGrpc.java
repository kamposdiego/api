package bhsg.com.api.service;

import bhsg.com.api.exceptions.CacheServiceUnavailble;
import bhsg.com.api.logging.LoggingConstants;
import bhsg.com.cache.IdempotentExistsRequest;
import bhsg.com.cache.IdempotentRequest;
import bhsg.com.cache.IdempotentServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import static bhsg.com.api.logging.LogContextUtils.trace;

@Slf4j
@Service
public class CacheServiceGrpc {

    @GrpcClient("idempotentService")
    private IdempotentServiceGrpc.IdempotentServiceBlockingStub idempotentServiceBlockingStub;

    public Boolean existsById(final String requestId){
        try{
            return idempotentServiceBlockingStub.existsById(IdempotentExistsRequest
                    .newBuilder()
                    .setId(requestId)
                    .build()).getExists();
        } catch (final StatusRuntimeException statusRuntimeException){
            throw new CacheServiceUnavailble(requestId, statusRuntimeException);
        }
    }

    public void createPostRequest(String requestId){
        try{
            log.debug("{} {} Request ID {} is going to send to cache-service - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.IDEMPOTENT, requestId, trace());
            final var postRequest = idempotentServiceBlockingStub.save(IdempotentRequest
                    .newBuilder()
                    .setId(requestId)
                    .build());
            log.debug("{} {} Request ID {} was handled by cache-service - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.IDEMPOTENT, postRequest.getId(), trace());
        } catch (final StatusRuntimeException statusRuntimeException){
            log.error("{} {} Request ID {} wasn't handled by cache-service - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.ERROR, requestId, trace());
            throw new CacheServiceUnavailble(requestId, statusRuntimeException);
        }
    }

}
