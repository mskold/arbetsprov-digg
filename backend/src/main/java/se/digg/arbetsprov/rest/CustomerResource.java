package se.digg.arbetsprov.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import se.digg.arbetsprov.model.Customer;
import se.digg.arbetsprov.service.CustomerService;

import java.util.List;

@Path("/customers")
public class CustomerResource {

    @Inject
    private CustomerService customerService;
    private static final Logger log = Logger.getLogger(CustomerResource.class);

    /**
     * GET-anrop. Laddar alla kunder.
     * @return En lista som innehåller alla kunder.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getCustomers() {
        log.info("Request to list all customers.");
        return customerService.getAllCustomers();
    }

    /**
     * GET-anrop. Laddar en enskild kund med angivet id.
     * @param id Kundens id.
     * @return Response med kundens information, eller HTTP-status 404 om kunden saknas.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") long id) {
        log.infof("Request to show customer with id %d", id);
        return customerService.getCustomerById(id)
                .map(customer -> Response.ok(customer).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found with id: " + id)
                        .build());
    }

    /**
     * POST-anrop för att skapa en ny kund.
     * Skickar tillbaka den nyskapade kunden och HTTP-status 201 (Created) eller 400 (Bad request) om payload innehåller otillåten data.
     * I överiga fall, 500 (Internal Server Error) om något går fel.
     * @param customer Tar emot JSON med information om den nya kunden.
     * @return HTTP-status för att signalera resultat.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomer(Customer customer) {
        log.infof("Request to create new customer: %s", customer);
        return customerService.addCustomer(customer)
                .map(c-> Response.status(Response.Status.CREATED).entity(c).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).entity("Assigning ID to new customers is not allowed in POST. Use PUT instead.").build());
    }

    /**
     * POST-anrop. Bekvämlighetsfunktion för att lägga till flera kunder på en och samma gång.
     * TODO: Lägg till bättre felhantering.
     * @param customers Tar emot en JSON-lista med kundinformation.
     * @return HTTP-status 200 (Ok).
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/batch")
    public Response addCustomers(List<Customer> customers) {
        log.infof("Populating service with %d customers.", customers.size());
        for (Customer customer: customers) {
            customerService.addCustomer(customer);
        }
        return Response.ok("Added customers").build();
    }

    /**
     * PUT-anrop. Uppdaterar information om en kund.
     * Den returnerar HTTP-status 200 (Ok) och kundens uppdaterade information, eller 400 (Bad Request) om ID i path och body inte matchar.
     * Övriga fel ger status 500 (Internal server error).
     * @param id kundens id fås genom path.
     * @param customer JSON-payload med uppdaterad kundinformation.
     * @return HTTP-status för att signalera resultat.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") long id, Customer customer) {
        return customerService.updateCustomer(id, customer)
                .map(c -> Response.status(Response.Status.OK).entity(c).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).entity("ID in path and body must match.").build());
    }

    /**
     * DELETE-anrop mot en path med ID raderar kunden med motsvarande ID.
     * @param id ID för den kund som ska raderas.
     * @return HTTP-status 204 (No content) om operationen lyckades, eller 404 (Not found) om det inte finns någon kund med angivet ID.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") long id) {
        log.infof("Request to remove customer with id %d.", id);
        boolean removed = customerService.deleteCustomer(id);
        if (removed) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer with id " + id + " not found.")
                    .build();
        }
    }
}
