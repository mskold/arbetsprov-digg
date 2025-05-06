package se.digg.arbetsprov.health;

import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import se.digg.arbetsprov.service.CustomerService;

@Liveness
public class CustomerAPIHealthCheck implements HealthCheck {

    private static final long startTime = System.currentTimeMillis();
    @Inject
    private CustomerService customerService;
    @Inject
    private Uptime uptime;

    /**
     * Custom health-response. Visar systemets status samt uptime och hur många kunder som finns i systemet.
     * @return
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse
                .named("customer-backend")
                .status(isHealthy())
                .withData("uptime", uptime.getUptime())
                .withData("customer-count", customerService.customerCount())
                .build();
    }

    /**
     * Metod för att rapportera tjänstens allmäntillstånd.
     * TODO: Implementera faktiskt funktionalitet istället för att bara returnera "true".
     * @return true
     */
    private boolean isHealthy() {
        return true;
    }

}