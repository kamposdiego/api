package bhsg.com.api.service;

import bhsg.com.api.entity.PostRequestRedisHash;
import bhsg.com.api.exceptions.RedisErrorDuringSavingException;
import bhsg.com.api.exceptions.RedisSerializationException;
import bhsg.com.api.exceptions.RedisServiceUnavailableException;
import bhsg.com.api.logging.LoggingConstants;
import bhsg.com.api.repository.PostRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import static bhsg.com.api.logging.LogContextUtils.trace;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryService {

    private static final String FIND_OPERATION = "find";
    private static final String PERSIST_OPERATION = "persist";
    private static final String LOG_MESSAGE_FOR_REDIS = "{} {}{} Failed to {} request ID {} - {}";

    private final PostRequestRepository postRequestRepository;

    public void createPostRequest(final String requestId){
        try {
            postRequestRepository.save(new PostRequestRedisHash(requestId));
        } catch (RedisConnectionFailureException redisConnectionFailureException) {
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, PERSIST_OPERATION, requestId, trace(), redisConnectionFailureException);
            throw new RedisServiceUnavailableException("Redis server is down or unreachable", redisConnectionFailureException);
        } catch (final SerializationException serializationException){
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, PERSIST_OPERATION, requestId, trace(), serializationException);
            throw new RedisSerializationException("Can’t (de)serialize an object", serializationException);
        } catch (final RedisSystemException redisSystemException){
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, PERSIST_OPERATION, requestId, trace(), redisSystemException);
            throw new RedisErrorDuringSavingException("General Redis failures", redisSystemException);
        }
    }

    public Boolean existsById(final String requestId){
        try {
            return postRequestRepository.existsById(requestId);
        } catch (RedisConnectionFailureException redisConnectionFailureException) {
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, FIND_OPERATION, requestId, trace(), redisConnectionFailureException);
            throw new RedisServiceUnavailableException("Redis server is down or unreachable", redisConnectionFailureException);
        } catch (final SerializationException serializationException){
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, FIND_OPERATION, requestId, trace(), serializationException);
            throw new RedisSerializationException("Can’t (de)serialize an object", serializationException);
        } catch (final RedisSystemException redisSystemException){
            log.error(LOG_MESSAGE_FOR_REDIS, LoggingConstants.Symbols.ERROR, LoggingConstants.Tags.REDIS, LoggingConstants.Tags.IDEMPOTENT, FIND_OPERATION, requestId, trace(), redisSystemException);
            throw new RedisErrorDuringSavingException("General Redis failures", redisSystemException);
        }
    }

}
