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


public class EmployeeGetTest {
	
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
          //  .body("$.firstName", equalTo("John")) //invalid expression
            .body("lastName", equalTo("Doe"))
            .body("city", equalTo("New York"))  // Assert the city value
            .body("department", equalTo("IT"));

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
    
    @Test
    public void testGetEmployeesByName() {
        given()
            .queryParam("firstName", "John")
            .queryParam("lastName", "Doe")
        .when()
            .get("/search/name")
        .then()
            .statusCode(200)
            .body("[0].firstName", equalTo("John"))
           // .body("$[0].firstName", equalTo("John")) invalid expression
            .body("[0].lastName", equalTo("Doe"));
    }
    
    @Test
    public void testGetEmployeesByNonexistentName() {
        //should return response with no content
    	given()
            .queryParam("firstName", "Johnj")
            .queryParam("lastName", "Doed")
        .when()
            .get("/search/name")
        .then()
            .statusCode(204);
    }
    
    @Test
    public void testGetAllEmployees() {
        when()
            .get()
        .then()
            .statusCode(200) // Expecting HTTP 200 OK
            .body("$.size()", greaterThan(0)); // Ensure response contains employees
    }
}
