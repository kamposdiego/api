package bhsg.com.api.exceptions;

public final class NotFoundException extends RuntimeException {

    private static final String NOT_FOUND_THE_RESOURCE_WITH_ID_S = "Not Found the resource with ID %s";

    public NotFoundException(final String id){
        super(NOT_FOUND_THE_RESOURCE_WITH_ID_S.formatted(id));
    }

}
