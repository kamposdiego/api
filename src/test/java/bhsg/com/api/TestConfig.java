package bhsg.com.api;

import bhsg.com.api.repository.PostRequestRepository;
import bhsg.com.api.service.RDBMSService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
    public PostRequestRepository postRequestRepository() {
        return mock(PostRequestRepository.class);
    }

}
