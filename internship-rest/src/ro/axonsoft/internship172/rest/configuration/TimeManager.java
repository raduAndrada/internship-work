package ro.axonsoft.internship172.rest.configuration;

import java.time.Instant;

@FunctionalInterface
public interface TimeManager {

    Instant instant();

}
