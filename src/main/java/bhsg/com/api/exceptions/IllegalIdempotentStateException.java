package bhsg.com.api.exceptions;

public class IllegalIdempotentStateException extends IllegalStateException {

    private static final String ILLEGAL_STATE_OF_RESOURCE_WITH_ID = "The request ID %s was processed, but the data does not exist in the database.";

    public IllegalIdempotentStateException(final String id){
        super(ILLEGAL_STATE_OF_RESOURCE_WITH_ID.formatted(id));
    }

}
