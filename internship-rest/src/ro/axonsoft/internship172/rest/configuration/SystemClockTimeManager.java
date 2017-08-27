package ro.axonsoft.internship172.rest.configuration;

import java.time.Instant;

public class SystemClockTimeManager implements TimeManager {

    @Override
    public Instant instant() {
        return Instant.now();
    }

}
