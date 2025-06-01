package bhsg.com.api.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.slf4j.MDC;

import java.util.Optional;

import static bhsg.com.api.web.RequestAndCorrelationIdFilter.CORRELATION_ID_HEADER;
import static bhsg.com.api.web.RequestAndCorrelationIdFilter.REQUEST_ID_HEADER;

@Slf4j
@GrpcGlobalClientInterceptor
public class GrpcTraceClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        final var requestId = Optional.ofNullable(MDC.get(REQUEST_ID_HEADER)).orElse("N/A");
        final var correlationId = Optional.ofNullable(MDC.get(CORRELATION_ID_HEADER)).orElse("N/A");

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Metadata metadata = new Metadata();
                Metadata.Key<String> requestIdKey = Metadata.Key.of(REQUEST_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);
                Metadata.Key<String> correlationIdKey = Metadata.Key.of(CORRELATION_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);

                metadata.put(requestIdKey, requestId);
                metadata.put(correlationIdKey, correlationId);
                headers.merge(metadata);

                super.start(responseListener, headers);
            }
        };
    }
}
