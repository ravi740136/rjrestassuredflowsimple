package rj.restassured.flow.test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class EmployeeDeleteTest extends UpdateSetup{
    @Test(priority = 1)
    public void loginAndGetSession() {
        // Login request body
        Map<String, String> loginData = new HashMap<>();
        loginData.put("firstName", "admin");
        loginData.put("lastName", "admin");

        // Perform login
        Response response = given()
            .contentType(ContentType.JSON)
            .body(loginData)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract()
            .response();

        // Extract session cookie
        sessionId = response.getCookie("SESSIONID");
        Assert.assertNotNull(sessionId, "Session ID should not be null");
    }

    @Test(priority = 3, dependsOnMethods = "loginAndGetSession")
    public void deleteEmployeeWithValidSession() {
        RestAssured.requestSpecification = RestAssured.given()
                .accept(ContentType.TEXT);
        given()
            .cookie("SESSIONID", sessionId) // Set session ID
        .when()
            .delete("/" + employeeId) // DELETE request
        .then()
            .statusCode(200) // Expecting 200 OK
            .body(equalTo("Employee deleted successfully"));
        
        
        given()
        .cookie("SESSIONID", sessionId) // Set session ID
    .when()
        .delete("/" + employeeId) // DELETE request
    .then()
        .statusCode(404) 
        .body(equalTo("Employee not found"));
    }
    
    @Test(priority = 4, dependsOnMethods = "loginAndGetSession")
    public void deleteEmployeeWithoutSession() {
       // RestAssured.requestSpecification = RestAssured.given()
        //        .accept(ContentType.TEXT);
        given()
            //.cookie("SESSIONID", sessionId) // Set session ID
        .when()
            .delete("/" + employeeId)
        .then()
        .statusCode(401);
    }
    
    @Test(priority = 2)
    public void deleteEmployeeWithInvalidSessionId() {
        // Perform DELETE request with an invalid session ID
        given().contentType(ContentType.JSON)
               .cookie("SESSIONID", "invalid-session-id")
               .when().delete("/" + employeeId)
               .then().statusCode(403) // Forbidden
               .body(equalTo("Invalid session ID or not an admin"));
    }
    
    @Test(priority = 4, dependsOnMethods = "loginAndGetSession")
    public void deleteEmployeeThatDoesNotExist() {
        // Perform DELETE request for non-existing employee
        given().contentType(ContentType.JSON)
               .cookie("SESSIONID", sessionId)
               .when().delete("/9999") // Assuming 9999 is an invalid employee ID
               .then().statusCode(404) // Not Found
               .body(equalTo("Employee not found"));
    }
    
    @Test(priority = 5, dependsOnMethods = "loginAndGetSession")
    public void deleteAdminByFirstAndLastNameSuccessfully() {
        // Perform DELETE request for existing employee
        given().contentType(ContentType.JSON)
               .cookie("SESSIONID", sessionId)
               .when().delete("/name/admin/admin") // Assuming "John Doe" exists
               .then().statusCode(200) // OK
               .body(equalTo("Employee deleted successfully"));
    }
    
    @Test(priority = 6, dependsOnMethods = "deleteAdminByFirstAndLastNameSuccessfully")
    public void deleteEmployeeByFirstAndLastNameWhenAdminAccountNotFound() {
        // Simulate admin account not being found
        given().contentType(ContentType.JSON)
               .cookie("SESSIONID", sessionId)
               .when().delete("/name/John/Doe")
               .then().statusCode(403) // Forbidden
               .body(equalTo("Admin account not found"));
        
        String requestBody = "{\n" +
                "  \"firstName\": \"admin\",\n" +
                "  \"lastName\": \"admin\",\n" +
                "  \"city\": \"New York\",\n" +  // Added city field
                "  \"department\": \"IT\"\n" + 
                "}";

        given()
        .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
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
    
    @Test(priority = 7)
    public void deleteAllEmployees() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete()
        .then()
            .statusCode(200) // OK
            .body(equalTo("All employees deleted successfully"));
    }

}
