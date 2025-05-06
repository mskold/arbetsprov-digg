package se.digg.arbetsprov.repository;

import jakarta.enterprise.context.ApplicationScoped;
import se.digg.arbetsprov.model.Customer;

import java.util.*;

/**
 * In-memory repository.
 * Den här implementation hade lika gärna kunna leva i Servicelagret för just ett så här enkelt exempel,
 * men jag tänker att jag bygger ett simpelt repository för att visa ungefär jag skulle ha lagt affärslogik osv.
 *
 * Jag har i de flesta fall använt {@code Optional<Customer>} för att signalera att en kund inte nödvändigtvis finns,
 * vilket gör det lättare att hantera null-fall hela vägen upp till REST-implementationen.
 */
@ApplicationScoped
public class CustomerRepository {
    private static final Map<Long, Customer> customers = new HashMap<>();
    private static long idCounter = 1;

    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    public Optional<Customer> findById(long id) {
        return Optional.ofNullable(customers.get(id));
    }

    public long count() {
        return customers.size();
    }

    /**
     * Säkerställer att kunden har ett ID innan den sparar den till "databasen".
     * @throws IllegalArgumentException om en kund saknar ID.
     */
    public Optional<Customer> save(Customer customer) {
        if (customer.id() <= 0) {
            throw new IllegalArgumentException("Customer ID must be greater than 0");
        }
        customers.put(customer.id(), customer);
        return Optional.of(customer);
    }

    public boolean deleteById(long id) {
        return (customers.remove(id) != null);
    }

    /**
     * Säkerställ att kunden får ett unikt ID.
     * Det finns bättre sätt att göra det här med t.ex. Panache eller GeneratedValue i JPA.
     */
    public synchronized long getNextAvailableId() {
        while (customers.containsKey(idCounter)) {
            idCounter++;
        }
        return idCounter;
    }
}
