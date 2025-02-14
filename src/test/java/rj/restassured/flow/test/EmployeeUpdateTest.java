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

public class EmployeeUpdateTest extends UpdateSetup {

	@Test(priority = 1)
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

	@Test(priority = 2, dependsOnMethods = "loginAndGetSession")
	public void updateEmployeeWithValidSession() {
		// alternate for validating for text type response, to avoid the failure
		// Set RestAssured to always accept text/plain responses
	//	RestAssured.requestSpecification = RestAssured.given().accept(ContentType.TEXT);
		// New employee data
		//RestAssured.requestSpecification = null;  // Clears all cached configurations

		Map<String, Object> updatedEmployee = new HashMap<>();
		updatedEmployee.put("id", employeeId);
		updatedEmployee.put("firstName", "John2");
		updatedEmployee.put("lastName", "Doe2");
		updatedEmployee.put("department", "HR");
		updatedEmployee.put("city", "New York");

		// Perform PUT request with session
		given().contentType(ContentType.JSON).cookie("SESSIONID", sessionId).body(updatedEmployee).when()
		//.accept(ContentType.JSON)		
		.put("/" + employeeId).then()
				// .contentType(ContentType.TEXT)
				.statusCode(200) // Expecting OK status
				.body(equalTo("Employee updated successfully"));
	}

	@Test(priority = 3)
	public void updateEmployeeWithoutSessionShouldFail() {
		// RestAssured.requestSpecification = RestAssured.given()
		// .accept(ContentType.TEXT);
		// New employee data
		Map<String, Object> updatedEmployee = new HashMap<>();
		updatedEmployee.put("id", employeeId);
		updatedEmployee.put("firstName", "John2");
		updatedEmployee.put("lastName", "Doe2");
		updatedEmployee.put("department", "HR");
		updatedEmployee.put("city", "New York");

		// Perform PUT request without session
		given().contentType(ContentType.JSON).body(updatedEmployee).when().put("/" + employeeId).then()
				// .contentType(ContentType.TEXT)
				.statusCode(401); // Expecting Unauthorized
	}
}
