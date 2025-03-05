package rj.restassured.flow.test.filters;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import io.restassured.path.xml.config.XmlPathConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ResponseOptions;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestAssuredFiltersTest {

    private static RequestSpecification requestSpec;
    private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple/employees";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;

        // Configure Request Specification
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.requestSpecification=requestSpec;

        // Enable Logging if Validation Fails
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
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
    
    @BeforeMethod(dependsOnMethods = "deleteAllEmployees")
    public void testRegisterEmployee() {
        String employeeJson = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "department": "IT",
                    "city": "New York"
                }
                """;

        testId =    given()

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
    /**
     * 🔹 Test to demonstrate RequestLoggingFilter (logs request details).
     */
    @Test
    public void testRequestLoggingFilter() {
        given()
            .spec(requestSpec)
            .filter(new RequestLoggingFilter()) // Logs request details
        .when()
            .get()
        .then()
            .statusCode(200);
    }

    /**
     * 🔹 Test to demonstrate ResponseLoggingFilter (logs response details).
     */
    @Test
    public void testResponseLoggingFilter() {
        given()
            
            .filter(new ResponseLoggingFilter()) // Logs response details
        .when()
            .get()
        .then()
            .statusCode(200);
    }

    /**
     * 🔹 Test to demonstrate ErrorLoggingFilter (logs response details on failure).
     */
    @Test
    public void testErrorLoggingFilter() {
        given()           
        //    .filter(new ErrorLoggingFilter()) // Logs error response if the test fails
        .when()
            .get("/9999") // This will trigger an error
        .then()
            .statusCode(404);
        //check error logging filter both when test case passes and fails
    }

    /**
     * 🔹 Test to demonstrate a Custom Filter that adds a Header to Requests.
     */
    @Test
    public void testCustomRequestFilter() {
        given()
            .spec(requestSpec)
            .filter(new CustomRequestFilter()) // Adds custom headers
        .when()
            .get()
        .then()
            .statusCode(200);
    }

    /**
     * 🔹 Test to demonstrate a Custom Response Filter that modifies response before assertion.
     */
    @Test
    public void testCustomResponseFilter() {
      Response response =   given()
        .log().all()
            .spec(requestSpec)
            .filter(new ResponseLoggingFilter())
            .filter(new CustomResponseFilter()) // Modifies response
           
        .when()
            .get()
        .then()
            .statusCode(200).extract().response();
      
    }

    /**
     * 🔹 Custom Request Filter - Adds a header to all requests.
     */
     public static class CustomRequestFilter implements Filter {

		@Override
		public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
				FilterContext ctx) {
		    System.out.println("Custom Filter: Adding Authorization Header...");
            requestSpec.header("Authorization", "Bearer dummyToken123");

            return ctx.next(requestSpec, responseSpec);
		}
    }
     
     public class CustomLoggingFilter implements Filter {
    	    @Override
    	    public Response filter(FilterableRequestSpecification requestSpec,
    	                           FilterableResponseSpecification responseSpec,
    	                           FilterContext ctx) {
    	        Response response = ctx.next(requestSpec, responseSpec);
    	  
    	        response.getHeaders().asList().forEach(header ->
    	            System.out.println(header.getName() + ": " + header.getValue())
    	        );

    	        return response;
    	    }
    	}


}
