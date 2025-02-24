package rj.restassured.flow.test.defaults;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.config.LogConfig;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

import java.util.HashMap;
import java.util.Map;

public class EmployeeControllerSpecTest {

    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;
    private static ResponseSpecification updateresponseSpec;
	
	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual base URL
    private static final String BASE_PATH = "/employees"; 
   
    @BeforeClass
    public void setup() {
        // Set request defaults globally
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/rjrestassuredflowsimple/employees";

        // Configure logging for better debugging
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

        // Define reusable request specification
        requestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .build();

        // Define reusable response specification
        responseSpec = new ResponseSpecBuilder()
                .expectContentType("application/json")
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThan(2000L)) // Response should be under 2 seconds
                .build();

        updateresponseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.TEXT)
                .expectStatusCode(200).build();
                		
        RestAssured.filters(
                new RequestLoggingFilter(), 
                new ResponseLoggingFilter()
        );
    }
    
    @Test
    public void deleteAllEmployees() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete()
        .then()
            .statusCode(200) // OK
            .body(equalTo("All employees deleted successfully"));
    }
    
    String testid;

    @Test(dependsOnMethods = "deleteAllEmployees")
    public void testRegisterEmployee() {
        String employeeJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "department": "IT",
                    "city": "New York"
                }
                """;

        testid =    given()
            .spec(requestSpec)
            .body(employeeJson)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("department", equalTo("IT"))
        .extract().body().jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "testRegisterEmployee")
    public void testGetAllEmployees() {
        given()
            .spec(requestSpec)
        .when()
            .get()
        .then()
            .spec(responseSpec)
            .body("$.size()", greaterThan(0));
    }

    @Test(dependsOnMethods = "testRegisterEmployee")
    public void testGetEmployeeById() {
        given()
            .spec(requestSpec)
        .when()
            .get("/{id}", testid)
        .then()
            .spec(responseSpec)
            .body("firstName", notNullValue())
            .body("lastName", notNullValue());
    }

    @Test(dependsOnMethods = "testRegisterEmployee")
    public void testSearchEmployeeByDepartmentAndCity() {
        given()
            .spec(requestSpec)
            .queryParam("department", "IT")
            .queryParam("city", "New York")
        .when()
            .post("/search")
        .then()
            .spec(responseSpec)
            .body("$.size()", greaterThan(0));
    }
    
    private long checkIfEmployeeExists(String firstname, String lastname) {
    	Response r = given().queryParam("firstName", firstname).queryParam("lastName", lastname).when()
		.get("/search/name");
    int code = r.then().extract().statusCode();
    if (code==200) {
    	return r.jsonPath().getLong("id");
    }
    
    return -1l;

    }
    
    private Long createNewEmployeeAndGetId(String fname, String lname) {
        // New employee data
        Map<String, Object> newEmployee = new HashMap<>();
        newEmployee.put("firstName", fname);
        newEmployee.put("lastName", lname);
        newEmployee.put("department", "IT");
        newEmployee.put("city", "San Francisco");

        // Perform POST request to create new employee (without session ID)
        Response response = given()
            .contentType(ContentType.JSON)
            .body(newEmployee)
        .when()
            .post("/register")
        .then()
            .statusCode(201) // Expecting Created
            .extract()
            .response();

        return response.jsonPath().getLong("id"); // Extract new employee ID
    }
    String sessionId;
    public void loginAndGetSession() {
		// Login request body
		Map<String, String> loginData = new HashMap<>();
		loginData.put("firstName", "admin");
		loginData.put("lastName", "admin");

		// Perform login
		Response response = given().contentType(ContentType.JSON).body(loginData).when().post("/login").then()
				.statusCode(200).extract().response();

		// Extract session cookie
		sessionId = response.getCookie("SESSIONID");
		Assert.assertNotNull(sessionId, "Session ID should not be null");
	}

    @Test(dependsOnMethods = "testRegisterEmployee")
    public void testUpdateEmployee() {
        String updatedEmployeeJson = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "department": "HR",
                    "city": "San Francisco"
                }
                """;
       long employeeId = checkIfEmployeeExists("admin", "admin");
        if (employeeId == -1) {
        	createNewEmployeeAndGetId("admin", "admin");
        }
        loginAndGetSession();
        given()
            .spec(requestSpec)
            .cookie("SESSIONID", sessionId)
            .body(updatedEmployeeJson)
        .when()
            .put("/{id}", testid)
        .then()
            .spec(updateresponseSpec)
            .body(equalTo("Employee updated successfully"));
    }

    @Test(dependsOnMethods = "testUpdateEmployee", alwaysRun = true)
    public void testDeleteEmployee() {
    	 long employeeId = checkIfEmployeeExists("admin", "admin");
         if (employeeId == -1) {
         	createNewEmployeeAndGetId("admin", "admin");
         }
         loginAndGetSession();
    	
        given()
            .spec(requestSpec)
            .cookie("SESSIONID", sessionId)
        .when()
            .delete("/{id}", testid)
        .then()
        .spec(updateresponseSpec)
            .body(equalTo("Employee deleted successfully"));
    }
}

