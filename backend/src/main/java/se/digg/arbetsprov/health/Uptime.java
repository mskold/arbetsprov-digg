package se.digg.arbetsprov.health;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Liten tjänst som initieras vid uppstart och håller reda på hur länge applikationen varit igång.
 * Används av CustomerApiHealthCheck.
 */
@ApplicationScoped
public class Uptime {
    private long startTime;

    void onStart(@Observes StartupEvent ev) {
        startTime = System.currentTimeMillis();
    }

    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }
}
