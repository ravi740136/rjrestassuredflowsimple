package rj.restassured.flow.test.verifyextract;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class RestAssuredJsonPathTest {

	final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple";
	final String BASE_PATH = "/employees/data";
    
    @BeforeClass
	public void setup() {
		// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
		RestAssured.baseURI = BASE_URL;
		RestAssured.basePath = BASE_PATH;
		RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
	}

    @Test
    public void testJsonPathAssertions() {
        Response response =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("/")  // Fetch all employees
            .then()
                .statusCode(200)

                // Use index-based access for the second employee (index 1 for ID 102)
                .body("employees[1].id", equalTo(102))
                .body("employees[1].firstName", equalTo("Jane"))
                .body("employees[1].lastName", equalTo("Smith"))
                .body("employees[1].age", greaterThan(25))
                .body("employees[1].salary", lessThan(100000))
                .body("employees[1].department", equalTo("HR"))
                .body("employees[1].address.city", equalTo("Los Angeles"))
                .body("employees[1].address.zip", equalTo("90001"))

                // Use find method as an alternative approach
                .body("employees.find { it.id == 102 }.department", equalTo("HR"))
                .body("employees.find { it.id == 102 }.address.city", equalTo("Los Angeles"))
                .body("employees.find { it.id == 102 }.id", equalTo(102))
            .extract()
                .response();

        // Extract department name and verify
        String departmentName = response.jsonPath().getString("employees[1].department");
        assertEquals("HR", departmentName);

        // Extract city and verify
        String city = response.jsonPath().getString("employees[1].address.city");
        assertEquals("Los Angeles", city);

        // Extract employee ID and verify
        int employeeId = response.jsonPath().getInt("employees[1].id");
        assertEquals(102, employeeId);
    }


}
