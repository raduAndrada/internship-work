package ro.axonsoft.internship172.business.impl.base;

import java.time.Instant;

@FunctionalInterface
public interface TimeManager {

    Instant instant();

}
