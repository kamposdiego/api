package bhsg.com.api.repository;

import bhsg.com.api.entity.PostRequestRedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRequestRepository extends CrudRepository<PostRequestRedisHash, String> {

}
