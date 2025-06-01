package bhsg.com.api.exceptions;

public class CacheServiceUnavailble extends RuntimeException {

    private static final String CACHE_SERVICE_UNAVAILABLE = "The request wasn't processed. The X-Request-ID %s";

    public CacheServiceUnavailble(final String id, final Throwable throwable){
        super(CACHE_SERVICE_UNAVAILABLE.formatted(id), throwable);
    }

}
