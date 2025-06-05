package bhsg.com.api.configuration;

import io.github.resilience4j.retry.Retry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RetryConfig {

    @Bean
    public Retry cacheRetry() {
        return Retry.ofDefaults("cacheServiceRetry");
    }

}
