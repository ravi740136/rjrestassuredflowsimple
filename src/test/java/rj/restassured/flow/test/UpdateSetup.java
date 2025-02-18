package rj.restassured.flow.test;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UpdateSetup {
	    
	     String sessionId;
	    long employeeId = 1L; // Assuming an existing employee ID
	    private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual base URL
	    private static final String BASE_PATH = "/employees";

	    @BeforeClass
	    public void setup() {
	        RestAssured.baseURI = BASE_URL;
	        RestAssured.basePath = BASE_PATH;
	        RestAssured.filters(
	                new RequestLoggingFilter(),
	                new ResponseLoggingFilter()
	        );

	        // Check if employee ID 1 exists, else create a new employee
	        employeeId  = checkIfEmployeeExists();

	        if (employeeId == -1) {
	            employeeId = createNewEmployeeAndGetId();
	            System.out.println("Created new employee with ID: " + employeeId);
	        } else {           
	            System.out.println("Using existing employee ID: " + employeeId);
	        }
	    }

	    private long checkIfEmployeeExists() {
	    	Response r = given().queryParam("firstName", "getemp_first2").queryParam("lastName", "getemp_last2").when()
			.get("/search/name");
	    int code = r.then().extract().statusCode();
	    if (code==200) {
	    	return r.jsonPath().getLong("id");
	    }
	    
	    return -1l;
 
	    }

	    private Long createNewEmployeeAndGetId() {
	        // New employee data
	        Map<String, Object> newEmployee = new HashMap<>();
	        newEmployee.put("firstName", "updateemp_first2");
	        newEmployee.put("lastName", "updateemp_last2");
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
}
