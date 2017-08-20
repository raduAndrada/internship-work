package ro.axonsoft.internship172.web.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorMessage;

public class RestServiceResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RestServiceResponseErrorHandler.class);

	private ObjectMapper objectMapper;

	@Inject
	public void setObjectMapper(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean hasError(final ClientHttpResponse response) throws IOException {
		final HttpStatus statusCode = response.getStatusCode();
		return statusCode.series() == HttpStatus.Series.CLIENT_ERROR
				|| statusCode.series() == HttpStatus.Series.SERVER_ERROR;
	}

	@Override
	public void handleError(final ClientHttpResponse response) throws IOException {
		final HttpStatus statusCode = response.getStatusCode();
		ErrorMessage errorMessage = null;
		try {
			errorMessage = objectMapper.readerFor(ErrorMessage.class).readValue(response.getBody());
		} catch (final Exception e) {
			LOG.debug("Not business error message format", e);
		}
		if (errorMessage == null) {
			throw new GenericRestCallException(statusCode, Optional.ofNullable(getResponseBody(response))
					.map(n -> n.get("message").asText()).orElse("Unknown error"));
		} else if (errorMessage.getProperties() == null) {
			throw new GenericRestCallException(statusCode, errorMessage.getMessage());
		} else {
			throw new BusinessException(errorMessage.getMessage(), errorMessage.getProperties());
		}

	}

	private JsonNode getResponseBody(final ClientHttpResponse response) {
		try {
			final InputStream responseBody = response.getBody();
			if (responseBody != null) {
				return objectMapper.readTree(responseBody);
			}
		} catch (final IOException ex) {
			LOG.trace("Failed to read response error response body", ex);
		}
		return null;
	}

}
