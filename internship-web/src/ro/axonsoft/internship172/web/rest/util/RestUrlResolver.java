package ro.axonsoft.internship172.web.rest.util;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestUrlResolver {

	private String urlBase;

	@Value("${internship.rest.base-url}")
	public void setUrlBase(final String urlBase) {
		this.urlBase = urlBase;
	}

	public URI resolveRestUri(final String... relativeUri) {
		final StringBuilder result = new StringBuilder(urlBase);
		for (final String patElem : relativeUri) {
			result.append('/').append(patElem);
		}
		return URI.create(result.toString());
	}
}
