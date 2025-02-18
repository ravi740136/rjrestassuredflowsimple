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

	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual
																							// base URL
	private static final String BASE_PATH = "/employees";

	@BeforeClass
	public void setup() {
		// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
		RestAssured.baseURI = BASE_URL;
		RestAssured.basePath = BASE_PATH;
		RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
	}

	long id;

	// Test case for GET request to fetch an employee by ID
	@Test
	public void testGetEmployeeById() {

		given().contentType(ContentType.JSON).when().delete();
		when().get().then().statusCode(204);

		String requestBody = "{\n" + "  \"firstName\": \"getemp_first2\",\n" + "  \"lastName\": \"getemp_last2\",\n"
				+ "  \"city\": \"New York\",\n" + // Added city field
				"  \"department\": \"IT\"\n" + "}";

		Response r = given().contentType("application/json").body(requestBody).when().post("/register");

		id = r.body().jsonPath().getLong("id");

		given()
				// .header("Cache-Control", "no-cache, no-store, must-revalidate")
				.pathParam("id", id).when().get("/{id}").then()
				// .contentType(ContentType.JSON)
				.statusCode(200).body("firstName", equalTo("getemp_first2"))
				// .body("$.firstName", equalTo("John")) //invalid expression
				.body("lastName", equalTo("getemp_last2")).body("city", equalTo("New York")) // Assert the city value
				.body("department", equalTo("IT"));
	}

	@Test
	public void testGetEmployeeByIdValidatetestng() {
		Response r = given()
				// .header("Cache-Control", "no-cache, no-store, must-revalidate")
				.pathParam("id", id).when().get("/{id}");

		Assert.assertEquals(r.statusCode(), 200);
		Assert.assertEquals(r.jsonPath().getString("firstName"), "getemp_first2");

	}

	@Test
	public void testGetEmployeeByIdNotFound() {
		Response r = given()
				// .header("Cache-Control", "no-cache, no-store, must-revalidate")
				.pathParam("id", 1001).when().get("/{id}");

		Assert.assertEquals(r.statusCode(), 404);

	}

	@Test
	public void testGetEmployeesByName() {
		given().queryParam("firstName", "getemp_first2").queryParam("lastName", "getemp_last2").when()
				.get("/search/name").then().statusCode(200).body("firstName", equalTo("getemp_first2"))
				// .body("$[0].firstName", equalTo("John")) invalid expression
				.body("lastName", equalTo("getemp_last2"));
	}

	@Test
	public void testGetEmployeesByNonexistentName() {
		// should return response with no content
		given().queryParam("firstName", "Johnj").queryParam("lastName", "Doed").when().get("/search/name").then()
				.statusCode(204);
	}

	@Test
	public void testGetAllEmployees() {
		when().get().then().statusCode(200) // Expecting HTTP 200 OK
				.body("$.size()", greaterThan(0)); // Ensure response contains employees
	}

	@Test
	public void testGetEmployeeByIdWithInvalidId_ShouldReturnBadRequest() {
		Response response = given().contentType(ContentType.JSON).when().get("/abc") // Passing an invalid non-numeric
																						// ID
				.then().extract().response();

		Assert.assertEquals(response.statusCode(), 400); // Expecting BAD REQUEST

		// Validate error response body
		Assert.assertEquals(response.jsonPath().getInt("status"), 400);
		Assert.assertEquals(response.jsonPath().getString("error"), "Invalid employee id");
		Assert.assertTrue(response.jsonPath().getString("message").contains("ID must be a numeric value.")); // Example
																												// message
	}

	@Test
	public void testGetEmployeeByIdWithNegativeId_ShouldReturnBadRequest() {
		Response response = given().contentType(ContentType.JSON).when().get("/-5") // Invalid negative ID
				.then().extract().response();

		Assert.assertEquals(response.statusCode(), 400); // Expecting BAD REQUEST

		// Validate error response body
		Assert.assertEquals(response.jsonPath().getInt("status"), 400);
		Assert.assertEquals(response.jsonPath().getString("error"), "Invalid employee id");
		Assert.assertTrue(response.jsonPath().getString("message").contains("Invalid ID. Must be a positive number.")); // Example
																														// message
	}

	@Test(priority = 10)
	public void testGetAllEmployeesWhenNoRecords() {

		given().contentType(ContentType.JSON).when().delete();
		when().get().then().statusCode(204); // Expecting HTTP 200 OK

	}
}
