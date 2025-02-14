package rj.restassured.flow.test;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
        employee.setFirstName("Chrisloc");
        employee.setLastName("Smithloc");
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
                .body("firstName", equalTo("Chrisloc"))
                .body("lastName", equalTo("Smithloc"))
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
    

    @Test
    public void testDuplicateEmployeeException() {
        // Creating request body with duplicate employee details
        String duplicateEmployeeJson = "{\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Doe\",\n" +
                "    \"city\": \"San Francisco\",\n" +
                "    \"department\": \"IT\"\n" +
                "}";

        // Test case for DuplicateEmployeeException (HTTP 409)
        given()
            .contentType(ContentType.JSON)
            .body(duplicateEmployeeJson)
        .when()
            .post("/register")  // Adjust the endpoint as per your application
        .then()
            .statusCode(409)  // Checking for Conflict (409)
            .body("status", equalTo(409))
            .body("error", equalTo("Conflict"))
            .body("message", containsString("Employee with the same first and last name already exists."));
    }

    @Test
    public void testInvalidEmployeeDataException() {
        // Creating request body with invalid employee data
        String invalidEmployeeJson = "{\n" +
                "    \"firstName\": \"\",\n" +  // Invalid first name
                "    \"lastName\": \"Doe\",\n" +
                "    \"city\": \"San Francisco\",\n" +
                "    \"department\": \"IT\"\n" +
                "}";

        // Test case for InvalidEmployeeDataException (HTTP 400)
        given()
            .contentType(ContentType.JSON)
            .body(invalidEmployeeJson)
        .when()
            .post("/register")  // Adjust the endpoint as per your application
        .then()
            .statusCode(400)  // Checking for Bad Request (400)
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("message", containsString("First name cannot be empty"));
    }

    @Test
    public void testInternalServerErrorWhenDepartmentIsNull() {
        // Creating request body with department set to null
        String invalidEmployeeJson = "{\n" +
                "    \"firstName\": \"unknown\",\n" +
                "    \"lastName\": \"unknown\",\n" +
                "    \"city\": \"San Francisco\",\n" +
                "    \"department\": null\n" +  // Simulating missing department
                "}";

        given()
            .contentType(ContentType.JSON)
            .body(invalidEmployeeJson)
        .when()
            .post("/register")  // Adjust endpoint as per your API
        .then()
            .statusCode(500)  // Expecting Internal Server Error
            .body("status", equalTo(500))
            .body("error", equalTo("Internal Server Error"))
           // .body("message", equalTo("An unexpected error occurred")) // Match exception handler message
            .body("timestamp", notNullValue()); // Ensure timestamp is present
    }
    
    @AfterClass
    public void cleanupDatabase() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete()  // Make sure this endpoint exists in your API
        .then()
            .statusCode(200);  // Assuming delete is successful
    }
}
