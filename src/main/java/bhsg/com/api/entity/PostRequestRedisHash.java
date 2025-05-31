package bhsg.com.api.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@Data
@RedisHash("PostRequest")
public class PostRequestRedisHash {
    @Id
    private String id;

    @TimeToLive
    private Long ttl = 10800L;

    public PostRequestRedisHash(final String id){
        this.id = id;
    }

}
