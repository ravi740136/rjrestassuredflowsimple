package rj.restassured.flow.test.logging;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;

public class EmployeeApiLoggingTest {

	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual
	// base URL
private static final String BASE_PATH = "/employees";

@BeforeClass
public void setup() {
// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
RestAssured.baseURI = BASE_URL;
RestAssured.basePath = BASE_PATH;
//RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
}

@BeforeMethod
public void deleteAllEmployees() {
    given()
        .contentType(ContentType.JSON)
    .when()
        .delete()
    .then()
        .statusCode(200) // OK
        .body(equalTo("All employees deleted successfully"));
}

String testId;
    @Test
    public void testCreateEmployeeWithFiltersDefaultLogging() {
        String employeeJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "department": "IT",
                "city": "New York"
            }
            """;

        given()
        //by default filters will log all
           // .log().all()  // Logs full request
            .header("Content-Type", "application/json")
            .body(employeeJson)
        .when()
            .post("/register")  // Replace with actual endpoint
        .then()
          //  .log().all()  // Logs full response
            .statusCode(201)
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("department", equalTo("IT"));
    }
    

    @Test
    public void testGetEmployeeWithHeadersAndConditionalLogging() {
       
    	 String employeeJson = """
    	            {
    	                "firstName": "John",
    	                "lastName": "Doe",
    	                "department": "IT",
    	                "city": "New York"
    	            }
    	            """;

    	    testId =    given()
    	    		 .contentType(ContentType.JSON)
    	        .body(employeeJson)
    	    .when()
    	        .post("/register")
    	    .then()
    	   
    	        .statusCode(201)
    	        .extract().body().jsonPath().getString("id");
    	
    	
    	given()
            .log().headers()  // Logs only headers, it works only if global filters are not specified
        .when()
           // .get("/test"+testId)  // Replace with actual employee ID
            .get("/test"+testId)
            .then()
            .log().ifError()  // Logs response only if there’s an error
            .statusCode(400);

    }

    @Test
    public void testGetEmployeeLogsIfValidationFails() {
    	 given()
			// .header("Cache-Control", "no-cache, no-store, must-revalidate")
			.pathParam("id", 1001)
			 .log().all()  
			.when().get("/{id}")
           // Logs only query/path parameters
       
        .then()
            .log().ifValidationFails()  // Logs response only if validation fails
            .statusCode(404);  // Expecting 404 Not Found
    }  
    
    @Test
    public void testCreateDummyEmployeeWithloggingParams() {
        given()
            .contentType(ContentType.MULTIPART) // Set content type
          .pathParam("id", "445")
            .queryParam("name", "Ravi") // Add form parameters
            .param("age", 30)
            .formParam("department", "IT")
            .multiPart("file", new File("src/test/resources/newfile.txt")) 
            .log().params() // Logs request details
        .when()
            .post("/dummy/{id}") // Send POST request
        .then()
            .log().all() // Logs response details
            .statusCode(201) // Verify response status
            .body(containsString("Employee created: Name=Ravi, Age=30, Department=IT")); // Validate response
    }
    
    
}
