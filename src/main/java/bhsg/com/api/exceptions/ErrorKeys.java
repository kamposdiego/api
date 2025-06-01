package bhsg.com.api.exceptions;

public enum ErrorKeys {
    NOT_FOUND("error.notfound"),
    INTERNAL("error.internalserver"),
    OPTIMISTIC("error.optimistic"),
    EMPTY_DATA_ACCESS("error.emptydataaccess"),
    INVALID_DATA_ACCESS("error.invaliddataaccess"),
    CONSTRAINT_VIOLATION("error.constraintviolation"),
    DATA_INTEGRITY("error.dataintegrity"),
    METHOD_ARGUMENT_NOT_VALID("error.methodargumentnotvalid"),
    METHOD_ARGUMENT_TYPE_MISMATCH("error.methodargumenttypemismatch"),
    CACHE_UNAVAILABLE("error.cacheunavailable"),
    ILLEGAL_IDEMPOTENT_STATE("error.illegalidempotentstate");

    final String baseKey;
    ErrorKeys(String baseKey) {
        this.baseKey = baseKey;
    }

    String title() { return baseKey + ".title"; }
    String detail() { return baseKey + ".detail"; }

}
