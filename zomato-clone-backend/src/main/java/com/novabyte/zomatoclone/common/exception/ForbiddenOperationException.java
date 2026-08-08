package com.novabyte.zomatoclone.common.exception;

/**
 * Thrown when an authenticated, correctly-ROLE'd user tries to act on a
 * resource they don't own — e.g. a RESTAURANT_OWNER editing someone
 * else's restaurant. Distinct from a plain 403 at the endpoint level
 * because Spring Security's hasRole() can't express "own resource only".
 */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
