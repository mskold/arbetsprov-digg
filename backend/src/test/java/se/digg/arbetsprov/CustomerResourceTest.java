package se.digg.arbetsprov;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
class CustomerResourceTest {

    static final String ENDPOINT_URL = "/customers";

    @BeforeEach
    void setup() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"id\": 1, \"name\": \"Första Kunden\"}")
                .when()
                .put(ENDPOINT_URL + "/1")
                .then()
                .statusCode(200);
    }


    /**
     * Raderar samtliga kunder efter varje kört test för att säkerställa ett förutsägbart tillstånd inför varje enskilt test.
     */
    @AfterEach
    void cleanup() {
        @SuppressWarnings("unchecked")  // Vi vet vad listan innehåller.
        List<Map<String, Object>> customers = given()
                .when().get(ENDPOINT_URL)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(List.class);

        for (Map<String, Object> customer : customers) {
            given()
                    .when().delete(ENDPOINT_URL + "/" + customer.get("id"))
                    .then()
                    .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }
    }


    /**
     * Testar att lista samtliga kunder.
     * Det ska bara finnas en kund i tjänsten (med id 1) om @BeforeEach har körts korrekt.
     */
    @Test
    void testListAllCustomers() {
        given()
          .when().get(ENDPOINT_URL)
          .then()
             .statusCode(200);
    }

    /**
     * Testar att det går att hämta en kund med känt ID-nummer.
     */
    @Test
    void testFetchOneCustomer() {
        given()
            .when()
                .get(ENDPOINT_URL+ "/1")
            .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", equalTo("Första Kunden"));
    }

    /**
     * Testar att skapa en ny kund.
     */
    @Test
    void testAddOneCustomer() {
        final String name = "Test Testsson";
        final String address = "Testvägen 1";
        final String email = "test@test.se";
        final String telephone = "123-4567";
        final String json = String.format("""
                {"name": "%s", "address": "%s", "email": "%s", "telephone": "%s"}
                """, name, address, email, telephone);
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(ENDPOINT_URL)
                .then()
                .statusCode(201)
                .body("name", equalTo(name))
                .body("address", equalTo(address))
                .body("email", equalTo(email))
                .body("telephone", equalTo(telephone));
    }

    /**
     * Testar att försöka skapa en kund via POST med tilldelat ID (ska EJ vara tillåtet).
     */
    @Test
    void testAddOneBadCustomer() {
        String json = "{\"id\": \"100\", \"name\": \"Busig Kund\"}";
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(ENDPOINT_URL)
                .then()
                .statusCode(400);
    }

    /**
     * Testar att uppdatera en kunds namn.
     */
    @Test
    void testUpdateCustomer() {
        String json = "{\"id\": \"1\", \"name\": \"Riktigt Namn\"}";
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put(ENDPOINT_URL+"/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", equalTo("Riktigt Namn"));
    }

    /**
     * Testar att göra en uppdatering till ett icke existerande kund-ID.
     */
    @Test
    void testBadUpdateCustomer() {
        String json = "{\"id\": \"1\", \"name\": \"Riktigt Namn\"}";
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put(ENDPOINT_URL+"/0")
                .then()
                .statusCode(400);
    }

    /**
     * Testar att felaktigt ID i payload inte slår igenom, utan att det är till den URL man PUT:ar som räknas.
     */
    @Test
    void testUpdateCustomerWrong() {
        String json = "{\"id\": \"4\", \"name\": \"Riktigt Namn\"}";
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put(ENDPOINT_URL+"/1")
                .then()
                .statusCode(400);
    }

    /**
     * Testar att radera en kund.
     */
    @Test
    void testDeleteCustomer() {
        // Förvänta rätt statuskod vid framgångsrik DELETE.
        given().when().delete(ENDPOINT_URL+"/1").then().statusCode(204);
        // Förvänta NOT FOUND för att verifiera att kund faktiskt tagits bort.
        given().when().delete(ENDPOINT_URL+"/1").then().statusCode(404);
    }

    /**
     * Testar att lägga till flera kunder via batch-endpoint.
     */
    @Test
    void testBatchAddCustomers() {
        String json = "[{\"name\": \"Bobbo\"},{\"name\": \"Bibbi\"}]";

        // Lägg till flera kunder på samma gång.
        given()
                .when().contentType(ContentType.JSON).body(json)
                .post(ENDPOINT_URL+"/batch").then().statusCode(200);

        // Verifiera listans storlek och namn
        given()
                .get(ENDPOINT_URL)
                .then()
                .statusCode(200)
                .body("size()", is(3)) // Innehåller tre kunder eftersom setup()-metoden redan lagt till en kund.
                .body("name", hasItems("Bobbo", "Bibbi"));

    }

}