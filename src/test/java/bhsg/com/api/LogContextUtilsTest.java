package bhsg.com.api;

import bhsg.com.api.logging.LogContextUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LogContextUtilsTest {

    @Test
    void shouldReturnTraceWithFallbackWhenMdcValuesAreMissing() {
        MDC.clear(); // limpa o contexto
        String trace = LogContextUtils.trace();

        assertThat(trace).isEqualTo("requestId=N/A, correlationId=N/A");
    }

    @Test
    void shouldReturnTraceWithMdcValuesPresent() {
        MDC.put("X-Request-ID", "abc123");
        MDC.put("X-Correlation-ID", "xyz789");

        String trace = LogContextUtils.trace();

        assertThat(trace).isEqualTo("requestId=abc123, correlationId=xyz789");
    }

}
