package rj.restassured.flow.test.verifyextract;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class JsonPathHamcrestTest {
    	final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple";
    	final String BASE_PATH = "/employees/data";
        
        @BeforeClass
    	public void setup() {
    		// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
    		RestAssured.baseURI = BASE_URL;
    		RestAssured.basePath = BASE_PATH;
    		RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    	}
    

    // 1️⃣ Basic JSONPath assertions - Single field validation
    @Test
    public void testBasicJsonPathAssertions() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("employees[0].firstName", equalTo("John"))
                .body("employees[1].lastName", equalTo("Smith"));
    }

    // 2️⃣ Validating array elements and size
    @Test
    public void testJsonArrayValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees.size()", equalTo(2)) // Checking array size
                .body("employees.firstName", hasItems("John", "Jane")) // Contains specific items
                .body("employees.age", everyItem(greaterThan(20))); // All ages should be > 20
    }

    // 3️⃣ Nested JSON validation (Checking city inside address)
    @Test
    public void testNestedJsonPathValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[0].address.city", equalTo("New York"))
                .body("employees[1].address.zip", equalTo("90001"));
    }

    // 4️⃣ Using find() to retrieve a specific employee based on a condition
    @Test
    public void testJsonPathFindMethod() {
        given()
                .when()
                .get("/")
                .then()
                .body("employees.find { it.id == 101 }.firstName", equalTo("John"))
                .body("employees.find { it.department == 'HR' }.salary", equalTo(50000));
    }

    // 5️⃣ Using findAll() to filter employees earning more than 50K
    @Test
    public void testJsonPathFindAllMethod() {
        given()
                .when()
                .get("/")
                .then()
                .body("employees.findAll { it.salary > 50000 }.size()", equalTo(1))
                .body("employees.findAll { it.salary > 50000 }[0].firstName", equalTo("John"));
    }

    // 6️⃣ Using collect() to extract a list of all first names
    @Test
    public void testJsonPathCollectMethod() {
        given()
                .when()
                .get("/")
                .then()
                .body("employees.collect { it.firstName }", hasItems("John", "Jane"));
    }
}
