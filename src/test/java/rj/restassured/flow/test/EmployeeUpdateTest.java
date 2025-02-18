package rj.restassured.flow.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

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

	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "updateemp_first22"); // Same as before
	    updatedEmployee.put("lastName", "updateemp_last22");
	    updatedEmployee.put("department", "IT");
	    updatedEmployee.put("city", "San Francisco");

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
	
	@Test(priority = 8)
	public void updateEmployeeWithInvalidSessionShouldFail() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "Hacker");
	    updatedEmployee.put("lastName", "User");
	    updatedEmployee.put("department", "HR");
	    updatedEmployee.put("city", "Unknown");

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", "invalid-session") // Invalid session
	        .body(updatedEmployee)
	    .when()
	        .put("/" + employeeId)
	    .then()
	        .statusCode(403) // Expecting Forbidden
	        .body(equalTo("Invalid session ID or not an admin"));
	}
	
	@Test(priority = 4, dependsOnMethods = "loginAndGetSession")
	public void updateNonExistentEmployeeShouldFail() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", 99999); // Non-existent employee ID
	    updatedEmployee.put("firstName", "Ghost");
	    updatedEmployee.put("lastName", "User");
	    updatedEmployee.put("department", "Unknown");
	    updatedEmployee.put("city", "NoCity");

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", sessionId)
	        .body(updatedEmployee)
	    .when()
	        .put("/99999") // ID that does not exist
	    .then()
	        .statusCode(404) // Expecting Not Found
	        .body(equalTo("Employee not found"));
	}
	
	@Test(priority = 5, dependsOnMethods = "loginAndGetSession")
	public void updateEmployeeWithInvalidDepartmentShouldFail() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "InvalidDept");
	    updatedEmployee.put("lastName", "User");
	    updatedEmployee.put("department", ""); // Empty department
	    updatedEmployee.put("city", "Los Angeles");

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", sessionId)
	        .body(updatedEmployee)
	    .when()
	        .put("/" + employeeId)
	    .then()
	        .statusCode(400) // Expecting Bad Request due to validation
	        .body("message", equalTo("Invalid input: Fields cannot be empty"));
	}
	
	@Test(priority = 5, dependsOnMethods = "loginAndGetSession")
	public void updateEmployeeWithInvalidEmployeeShouldFail() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "InvalidDept");
	    updatedEmployee.put("lastName", "User");
	    updatedEmployee.put("department", ""); // Empty department
	    updatedEmployee.put("city", "Los Angeles");

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", sessionId)
	        .body(updatedEmployee)
	    .when()
	        .put("/" + employeeId)
	    .then()
	        .statusCode(400) // Expecting Bad Request due to validation
	        .body("message",equalTo("Invalid input: Fields cannot be empty"));
	}
	
	@Test(priority = 6, dependsOnMethods = "loginAndGetSession")
	public void updateEmployeeWithMissingFieldsShouldFail() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "MissingFields");
	    // Missing lastName, department, and city

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", sessionId)
	        .body(updatedEmployee)
	    .when()
	        .put("/" + employeeId)
	    .then()
	        .statusCode(400) // Expecting Bad Request
	        .contentType(ContentType.JSON) // Ensure it's JSON response
	        .body("error", equalTo("Invalid Employee Data"))
	        .body("message", equalTo("Invalid input: Fields cannot be empty"))
	        .body("status", equalTo(400));
	}
	
	@Test(priority = 7, dependsOnMethods = "loginAndGetSession")
	public void updateEmployeeWithSameDataShouldReturnNotModified() {
	    Map<String, Object> updatedEmployee = new HashMap<>();
	    updatedEmployee.put("id", employeeId);
	    updatedEmployee.put("firstName", "updateemp_first22"); // Same as before
	    updatedEmployee.put("lastName", "updateemp_last22");
	    updatedEmployee.put("department", "IT");
	    updatedEmployee.put("city", "San Francisco");

	    given()
	        .contentType(ContentType.JSON)
	        .cookie("SESSIONID", sessionId)
	        .body(updatedEmployee)
	    .when()
	        .put("/" + employeeId)
	    .then()
	        .statusCode(304); // Expecting Not Modified	       
	}

}
