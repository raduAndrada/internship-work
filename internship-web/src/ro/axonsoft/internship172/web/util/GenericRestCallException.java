package ro.axonsoft.internship172.web.util;

import org.springframework.http.HttpStatus;

import com.google.common.base.MoreObjects;

public class GenericRestCallException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus statusCode;

    public GenericRestCallException(final HttpStatus statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("").add("message", getMessage()).add("statusCode", statusCode).toString();
    }
}
