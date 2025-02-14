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
}
