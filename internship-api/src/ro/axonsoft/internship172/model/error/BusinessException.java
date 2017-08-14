package ro.axonsoft.internship172.model.error;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ErrorProperties properties;

	public BusinessException(final String message, final Throwable cause, final ErrorProperties properties) {
		super(message, cause);
		this.properties = checkNotNull(properties, "Properties is mandatory");
	}

	public BusinessException(final String message, final ErrorProperties properties) {
		super(message);
		this.properties = checkNotNull(properties, "Properties is mandatory");

	}

	public ErrorProperties getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("").add("message", getMessage()).add("properties", properties).toString();
	}
}
