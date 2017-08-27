package ro.axonsoft.internship172.rest.configuration;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorMessage;
import ro.axonsoft.internship172.model.error.ErrorProperties.VarValue;
import ro.axonsoft.internship172.model.error.ImtErrorMessage;

/**
 * REST exception handlers defined at a global level for the application
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	/**
	 * Catch all for any other exceptions...
	 */
	@ExceptionHandler({ Exception.class })
	@ResponseBody
	public ResponseEntity<?> handleAnyException(final Exception e) {
		return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle failures commonly thrown from code
	 */
	@ExceptionHandler({ InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
			ConversionFailedException.class })
	@ResponseBody
	public ResponseEntity<?> handleMiscFailures(final Throwable t) {
		return errorResponse(t, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Send a 409 Conflict in case of concurrent modification
	 */
	@ExceptionHandler({ OptimisticLockingFailureException.class, DataIntegrityViolationException.class })
	@ResponseBody
	public ResponseEntity<?> handleConflict(final Exception ex) {
		return errorResponse(ex, HttpStatus.CONFLICT);
	}

	protected ResponseEntity<ErrorMessage> errorResponse(final Throwable throwable, final HttpStatus status) {
		if (null != throwable) {
			LOG.error("error caught: " + throwable.getMessage(), throwable);
			final ImtErrorMessage.Builder msgBuilder = ImtErrorMessage.builder().message(throwable.getMessage());
			if (throwable instanceof BusinessException) {
				final BusinessException businessException = (BusinessException) throwable;
				msgBuilder.properties(businessException.getProperties());
				if (businessException.getProperties().getVars().iterator().hasNext()) {
					final VarValue compare = businessException.getProperties().getVars().iterator().next();
					if (compare.getName().equals("password") || compare.getName().equals("username")) {
						return response(msgBuilder.build(), HttpStatus.FORBIDDEN);
					}
				}
			}
			return response(msgBuilder.build(), status);
		} else {
			LOG.error("unknown error caught in RESTController, {}", status);
			return response(null, status);
		}
	}

	protected <T> ResponseEntity<T> response(final T body, final HttpStatus status) {
		LOG.debug("Responding with a status of {}", status);
		return new ResponseEntity<>(body, new HttpHeaders(), status);
	}
}