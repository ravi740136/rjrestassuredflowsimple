package rj.restassured.flow.test.verifyextract;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import rj.restassured.flow.entity.NeoEmployee;
import rj.restassured.flow.entity.NeoEmployeeResponse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class JsonPathExtractionTest {
	final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple";
	final String BASE_PATH = "/employees/data";
    
    @BeforeClass
	public void setup() {
		// RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;
		RestAssured.baseURI = BASE_URL;
		RestAssured.basePath = BASE_PATH;
		RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
	}

    // 1️⃣ Extract a single value - Get first employee's firstName
    @Test
    public void testExtractSingleValue() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String firstName = jsonPath.getString("employees[0].firstName");
        
        assertEquals(firstName, "John", "First employee name should be John");
    }

    // 2️⃣ Extract a list - Get all employee first names
    @Test
    public void testExtractListValues() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> firstNames = jsonPath.getList("employees.firstName");

        assertTrue(firstNames.contains("John") && firstNames.contains("Jane"), "List should contain John and Jane");
    }

    // 3️⃣ Extract a map (employee details)
    @Test
    public void testExtractEmployeeMap() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        Map<String, Object> employee = jsonPath.getMap("employees[0]");

        assertEquals(employee.get("firstName"), "John");
        assertEquals(employee.get("department"), "IT");
    }

    // 4️⃣ Extract nested value - Get city of first employee
    @Test
    public void testExtractNestedValue() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String city = jsonPath.getString("employees[0].address.city");

        assertEquals(city, "New York", "City should be New York");
    }

    // 5️⃣ Extract using find() - Get first employee who is in IT department
    @Test
    public void testExtractUsingFind() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String employeeName = jsonPath.getString("employees.find { it.department == 'IT' }.firstName");

        assertEquals(employeeName, "John", "Employee in IT should be John");
    }

    // 6️⃣ Extract using findAll() - Get all employees older than 25
    @Test
    public void testExtractUsingFindAll() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> employees = jsonPath.getList("employees.findAll { it.age > 25 }.firstName");

        assertTrue(employees.contains("John") && employees.contains("Jane"), "Both employees should be older than 25");
    }

    // 7️⃣ Extract using collect() - Get all department names
    @Test
    public void testExtractUsingCollect() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> departments = jsonPath.getList("employees.collect { it.department }");

        assertTrue(departments.contains("IT") && departments.contains("HR"), "Departments should contain IT and HR");
    }
    
    

}
