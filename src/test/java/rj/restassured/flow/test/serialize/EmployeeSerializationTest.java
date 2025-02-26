package rj.restassured.flow.test.serialize;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import rj.restassured.flow.entity.Employee;

import static org.hamcrest.Matchers.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;

public class EmployeeSerializationTest {
	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual
	// base URL
private static final String BASE_PATH = "/employees";

@BeforeClass
public void setup() {
// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
RestAssured.baseURI = BASE_URL;
RestAssured.basePath = BASE_PATH;
RestAssured.requestSpecification = new RequestSpecBuilder()
.setContentType("application/json")
.build();
RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
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

String testid;

@Test
public void testSerializationCreateEmployee() {
    String employeeJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "department": "IT",
                "city": "New York"
            }
            """;

    testid =    given()

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

@Test
public void testCustomSerializationCreateEmployee() throws Exception {
    // Create an Employee object
    Employee employee = new Employee();
    employee.setFirstName("John");
    employee.setLastName("Doe");
    employee.setDepartment("IT");
    employee.setCity("New York");

    // Convert Employee object to JSON using Jackson ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    String employeeJson = objectMapper.writeValueAsString(employee);

    // Send POST request with serialized JSON
    testid = given()
                .body(employeeJson)
                .contentType("application/json")
            .when()
                .post("/register")
            .then()
                .statusCode(201)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("department", equalTo("IT"))
            .extract().body().jsonPath().getString("id");
}
@Test
public void testStoreEmployees_ComplexJson() {
    String employeeJson = """
        {
          "employees": [
            {
              "id": 101,
              "firstName": "John",
              "lastName": "Doe",
              "age": 35,
              "department": "IT",
              "salary": 75000,
              "active": true,
              "email": "john.doe@example.com",
              "joiningDate": "2023-05-15",
              "address": {
                "city": "New York",
                "zip": "10001"
              }
            },
            {
              "id": 102,
              "firstName": "Jane",
              "lastName": "Smith",
              "age": 28,
              "department": "HR",
              "salary": 50000,
              "active": false,
              "email": "jane.smith@example.com",
              "joiningDate": "2022-08-20",
              "address": {
                "city": "Los Angeles",
                "zip": "90001"
              }
            }
          ]
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(employeeJson)
    .when()
        .post("/store")
    .then()
        .statusCode(200)
        .body("employees", hasSize(2)) // Check if response contains 2 employees
        .body("employees[0].firstName", equalTo("John"))
        .body("employees[1].firstName", equalTo("Jane"))
        .body("employees[0].department", equalTo("IT"))
        .body("employees[1].department", equalTo("HR"));
}

}

