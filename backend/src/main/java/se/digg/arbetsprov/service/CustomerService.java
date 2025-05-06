package se.digg.arbetsprov.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import se.digg.arbetsprov.model.Customer;
import se.digg.arbetsprov.repository.CustomerRepository;

import java.util.*;

/**
 * En enkel customer-service.
 */
@ApplicationScoped
public class CustomerService {
    @Inject
    private CustomerRepository customerRepository;
    private static final Logger log = Logger.getLogger(CustomerService.class);

    /**
     * Hämtar alla kunder.
     * @return en List av Customer.
     */
    public List<Customer> getAllCustomers() {
        log.debug("Returning all customers.");
        return customerRepository.findAll();
    }

    /**
     * Skapar en ny Customer. Den nya kunden får ett nytt ID från en räknare i repositoriet.
     * @param customer Kunden som ska sparas.
     * @return En Optional innehållande den nyskapade Customern.
     */
    public Optional<Customer> addCustomer(Customer customer) {
        if (customer.id() != 0) {
            log.warn("Attempt to add customer with non-zero ID rejected.");
            return Optional.empty();
        }
        customer = assignCustomerId(customerRepository.getNextAvailableId(), customer);
        log.debugf("Adding customer %s", customer);
        return customerRepository.save(customer);
    }

    /**
     * Raderar en kund med angivet id.
     * @return true om operationen lyckades. Annars false.
     */
    public boolean deleteCustomer(long id) {
        boolean deleted = customerRepository.deleteById(id);
        if (deleted) {
            log.debugf("Successfully deleted customer %d.", id);
        } else {
             log.warnf("Tried to delete non-existing customer found with id %d", id);
        }
        return deleted;
    }

    /**
     * Uppdatera en kund med angivet ID.
     * @param id kundens id.
     * @param customer kundens data.
     * @return En Optional Customer.
     */
    public Optional<Customer> updateCustomer(long id, Customer customer) {
        if (id != customer.id()) {
            log.warnf("Customer object contained mismatched ID (%d != %d).", customer.id(), id);
            return Optional.empty();
        }
        return customerRepository.save(customer);
    }

    /**
     * Ladda en kund med angivet id.
     * @param id kundens id.
     * @return En Optional Customer.
     */
    public Optional<Customer> getCustomerById(long id) {
        return customerRepository.findById(id);
    }

    /**
     * Levererar hur många kunder som finns i systemet.
     * @return antalet kunder.
     */
    public long customerCount() {
        return customerRepository.count();
    }

    /**
     * Eftersom Customer är en Record så är den immutable, så vi har en bekvämlighetsmetod som skapar en ny kund med angivet ID.
     * @param id som ska tilldelas kunden.
     * @param customer Kunden.
     * @return Den nyskapade kunden
     */
    private Customer assignCustomerId(long id, Customer customer) {
        return new Customer(id, customer.name(), customer.address(), customer.email(), customer.telephone());
    }
}
