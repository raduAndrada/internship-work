package ro.axonsoft.internship172.business.impl.base;

import java.time.Instant;

public class SystemClockTimeManager implements TimeManager {

    @Override
    public Instant instant() {
        return Instant.now();
    }

}
