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

public class EmployeeTest {
	
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
    @Test
    public void testRegisterEmployee() {
        String requestBody = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"city\": \"New York\"\n" +  // Added city field
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
            .body("city", equalTo("New York"));  // Assert the city value
    }
    
    @Test
    public void testCreateEmployeeAndVerifyLocation() {
        // Create Employee object
        Employee employee = new Employee();
        employee.setFirstName("Chris");
        employee.setLastName("Smith");
        employee.setCity("Star City");

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
                .body("city", equalTo("Star City"));
    }

    // Test case for GET request to fetch an employee by ID
    @Test
    public void testGetEmployeeById() {
        given()
    //    .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .pathParam("id", 1)
        .when()
            .get("/{id}")
        .then()
      //  .contentType(ContentType.JSON)
            .statusCode(200)
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("city", equalTo("New York"));  // Assert the city value
    }
    
    @Test
    public void testGetEmployeeByIdValidatetestng() {
     Response r =   given()
    //    .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .pathParam("id", 1)
        .when()
            .get("/{id}");
   
  
    Assert.assertEquals(r.statusCode(), 200);
    Assert.assertEquals(r.jsonPath().getString("firstName"), "John");
  
    }
}
