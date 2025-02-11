package rj.restassured.flow.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import rj.restassured.flow.entity.Employee;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class EmployeePostTest {
	
	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual base URL
    private static final String BASE_PATH = "/employees"; 
    
    @BeforeClass
    public void setup() {
    	//RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
        RestAssured.baseURI = BASE_URL;
        RestAssured.basePath=BASE_PATH;
        RestAssured.filters(
                new RequestLoggingFilter(), 
                new ResponseLoggingFilter()
        );
    }

    // Test case for POST request to register an employee with city
    @Test(priority = -2)
    public void testRegisterEmployee() {
        String requestBody = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"city\": \"New York\",\n" +  // Added city field
                "  \"department\": \"IT\"\n" + 
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("city", equalTo("New York")) // Assert the city value
            .body("department", equalTo("IT"));
    }
    
    // Test case for POST request to register an employee with city
    @Test(priority = -2)
    public void testRegisterEmployeeAdmin() {
        String requestBody = "{\n" +
                "  \"firstName\": \"admin\",\n" +
                "  \"lastName\": \"admin\",\n" +
                "  \"city\": \"New York\",\n" +  // Added city field
                "  \"department\": \"IT\"\n" + 
                "}";

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("admin"))
            .body("lastName", equalTo("admin"))
            .body("city", equalTo("New York")) // Assert the city value
            .body("department", equalTo("IT"));
    }
    
    @Test(priority = -1)
    public void testCreateEmployeeAndVerifyLocation() {
        // Create Employee object
        Employee employee = new Employee();
        employee.setFirstName("Chris");
        employee.setLastName("Smith");
        employee.setCity("Star City");
        employee.setDepartment("IT");

        // Send POST request
       // Response response = given()
        		 String location = given()
                .contentType("application/json")
                .body(employee)
                .when()
                .post("/registerloc")
                .then()
                .statusCode(201)  // Expect HTTP 201 Created
                .header("Location", notNullValue())  // Location header must exist
                .extract()
                .header("Location");//directly extracting header value

        // Extract Location header
      //  String location = response.getHeader("Location");
        System.out.println("Location Header: " + location);

        // Verify the newly created employee can be retrieved
        given()
                .when()
                .get(location)
                .then()
                .statusCode(200)  // Expect HTTP 200 OK
                .body("firstName", equalTo("Chris"))
                .body("lastName", equalTo("Smith"))
                .body("city", equalTo("Star City"))
                .body("department", equalTo("IT"));
    }
      
    @Test
    public void testSearchEmployees() {
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("department", "IT")
            .formParam("city", "New York")
        .when()
            .post("/search")
        .then()
            .statusCode(200)
            .body("[0].department", equalTo("IT"))
            .body("[0].city", equalTo("New York"));
    }
}
