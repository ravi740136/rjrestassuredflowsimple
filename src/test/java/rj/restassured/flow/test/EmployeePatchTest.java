package rj.restassured.flow.test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

public class EmployeePatchTest extends UpdateSetup {


	@Test
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

    @Test(dependsOnMethods = "loginAndGetSession")
    public void testPatchUpdateEmployee() {
        // Partial update - Change only the city
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("city", "Los Angeles");

        given()
                .contentType(ContentType.JSON)
                .cookie("SESSIONID", sessionId) // Authenticate
                .body(updateFields)
                .when()
                .patch("/" + employeeId)
                .then()
                .statusCode(200)
                .body(equalTo("Employee updated successfully"));
    
        given()
        .contentType(ContentType.JSON)
       
        .when()
        .get("/" + employeeId) // Fetch updated employee
        .then()
        .statusCode(200)
        .body("city", equalTo("Los Angeles")); // ✅ Verify city was updated
    
    }

    @Test
    public void testPatchUpdateEmployee_InvalidSession() {
        // Partial update with an invalid session
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("city", "San Francisco");

        given()
                .contentType(ContentType.JSON)
                .cookie("SESSIONID", "invalid-session") // Invalid session
                .body(updateFields)
                .when()
                .patch("/" + employeeId)
                .then()
                .statusCode(403) // Forbidden
                .body(equalTo("Invalid session ID or not an admin"));
    }

    @Test
    public void testPatchUpdateEmployee_NotFound() {
        // Attempt to update a non-existing employee
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("city", "Chicago");

        given()
                .contentType(ContentType.JSON)
                .cookie("SESSIONID", sessionId)
                .body(updateFields)
                .when()
                .patch("/99999") // Non-existent ID
                .then()
                .statusCode(404)
                .body(equalTo("Employee not found"));
    }

    @Test
    public void testPatchUpdateEmployee_EmptyRequest() {
        // Sending an empty JSON body (No updates)
        given()
                .contentType(ContentType.JSON)
                .cookie("SESSIONID", sessionId)
                .body("{}")
                .when()
                .patch("/" + employeeId)
                .then()
                .statusCode(200)
                .body(equalTo("Employee updated successfully")); // No fields changed
    }
}
