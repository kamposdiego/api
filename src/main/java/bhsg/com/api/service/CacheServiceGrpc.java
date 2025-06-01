package bhsg.com.api.service;

import bhsg.com.api.exceptions.CacheServiceUnavailble;
import bhsg.com.api.logging.LoggingConstants;
import bhsg.com.cache.IdempotentByXIdempotencyRequest;
import bhsg.com.cache.IdempotentRequest;
import bhsg.com.cache.IdempotentServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static bhsg.com.api.logging.LogContextUtils.trace;

@Slf4j
@Service
public class CacheServiceGrpc {

    @GrpcClient("idempotentService")
    private IdempotentServiceGrpc.IdempotentServiceBlockingStub idempotentServiceBlockingStub;

    public Optional<UUID> existsById(final String xIdempotencyKey){
        try{
            final var as = idempotentServiceBlockingStub.getByXIdempotencyKey(IdempotentByXIdempotencyRequest
                    .newBuilder()
                    .setXIdempotencyKey(xIdempotencyKey)
                    .build());

            if(as.getId().isEmpty()){
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(as.getId()));
        } catch (final StatusRuntimeException statusRuntimeException){
            throw new CacheServiceUnavailble(xIdempotencyKey, statusRuntimeException);
        }
    }

    public void createPostRequest(final String xIdempotencyKey, final String id){
        try{
            log.debug("{} {} Request ID {} is going to send to cache-service - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.IDEMPOTENT, xIdempotencyKey, trace());
            final var postRequest = idempotentServiceBlockingStub.save(IdempotentRequest
                    .newBuilder()
                    .setXIdempotencyKey(xIdempotencyKey)
                    .setId(id)
                    .build());
            log.debug("{} {} Request ID {} was handled by cache-service - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.IDEMPOTENT, postRequest.getId(), trace());
        } catch (final StatusRuntimeException statusRuntimeException){
            log.error("{} {} Request ID {} wasn't handled by cache-service - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.ERROR, xIdempotencyKey, trace());
            throw new CacheServiceUnavailble(xIdempotencyKey, statusRuntimeException);
        }
    }

}
