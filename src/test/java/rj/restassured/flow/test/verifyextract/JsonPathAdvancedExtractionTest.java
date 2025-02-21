package rj.restassured.flow.test.verifyextract;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import rj.restassured.flow.entity.NeoEmployee;
import rj.restassured.flow.entity.NeoEmployeeResponse;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class JsonPathAdvancedExtractionTest {
    final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple";
    final String BASE_PATH = "/employees/data";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.basePath = BASE_PATH;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    // ✅ Extract Single Value - Get first employee's firstName
    @Test
    public void testExtractSingleValue() {
        Response response = given().when().get("/").then().statusCode(200).extract().response();
        String firstName = response.jsonPath().getString("employees[0].firstName");

        assertEquals(firstName, "John", "First employee name should be John");
    }

    // ✅ Extract List - Get all employee first names
    @Test
    public void testExtractListValues() {
        Response response = given().when().get("/").then().extract().response();
        List<String> firstNames = response.jsonPath().getList("employees.firstName");

        assertTrue(firstNames.contains("John") && firstNames.contains("Jane"), "Should contain John and Jane");
    }

    // ✅ Extract a Map - Get full employee details
    @Test
    public void testExtractEmployeeMap() {
        Response response = given().when().get("/").then().extract().response();
        Map<String, Object> employee = response.jsonPath().getMap("employees[0]");

        assertEquals(employee.get("firstName"), "John");
        assertEquals(employee.get("department"), "IT");
    }

    @Test
    public void testExtractNestedValue() {
        Response response = given().when().get("/").then().extract().response();
        String city = response.jsonPath().getString("employees[0].address.city");  // ✅ Use "address" instead of "neoAddress"

        assertEquals(city, "New York", "City should be New York");
    }

    // ✅ Extract using find() - Get first employee in IT department
    @Test
    public void testExtractUsingFind() {
        Response response = given().when().get("/").then().extract().response();
        String employeeName = response.jsonPath().getString("employees.find { it.department == 'IT' }.firstName");

        assertEquals(employeeName, "John", "Employee in IT should be John");
    }

    // ✅ Extract using findAll() - Get all employees older than 25
    @Test
    public void testExtractUsingFindAll() {
        Response response = given().when().get("/").then().extract().response();
        List<String> employees = response.jsonPath().getList("employees.findAll { it.age > 25 }.firstName");

        assertTrue(employees.contains("John") && employees.contains("Jane"), "Both employees should be older than 25");
    }

    // ✅ Extract using collect() - Get all department names
    @Test
    public void testExtractUsingCollect() {
        Response response = given().when().get("/").then().extract().response();
        List<String> departments = response.jsonPath().getList("employees.collect { it.department }");

        assertTrue(departments.contains("IT") && departments.contains("HR"), "Should contain IT and HR");
    }

    // ✅ Extract Numeric & Boolean values
    @Test
    public void testExtractNumericAndBooleanValues() {
        Response response = given().when().get("/").then().extract().response();
        JsonPath jsonPath = response.jsonPath();

        int salary = jsonPath.getInt("employees[0].salary");
        boolean isActive = jsonPath.getBoolean("employees[0].active");

        assertTrue(salary > 30000, "Salary should be greater than 30K");
        assertTrue(isActive, "Employee should be active");
    }

    // ✅ Extract and Validate Email Format using Regex
    @Test
    public void testExtractAndMatchPattern() {
        Response response = given().when().get("/").then().extract().response();
        String email = response.jsonPath().getString("employees[0].email");

        assertTrue(email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$"), "Invalid email format");
    }

    // ✅ Extract and Validate Date Format
    @Test
    public void testExtractDateFormat() {
        Response response = given().when().get("/").then().extract().response();
        String joiningDate = response.jsonPath().getString("employees[0].joiningDate");

        assertTrue(joiningDate.matches("^\\d{4}-\\d{2}-\\d{2}$"), "Date format should be YYYY-MM-DD");
    }

    @Test
    public void testExtractMultipleFields() {
        Response response = given().when().get("/").then().extract().response();
        System.out.println("Response: " + response.asString());  // Debugging output

        // Extract first employee details
        Map<String, Object> employee = response.jsonPath().getMap("employees[0]");
        Assert.assertNotNull(employee, "Employee data is null!");

        SoftAssert softAssert = new SoftAssert();
        
        softAssert.assertEquals(employee.get("firstName"), "John", "First Name mismatch");
        softAssert.assertEquals(employee.get("department"), "IT", "Department mismatch");
        softAssert.assertEquals(employee.get("salary"), 75000, "Salary mismatch"); // Updated correct salary

        // Check if "address" exists before accessing it
        if (employee.containsKey("address") && employee.get("address") != null) {
            Map<String, Object> address = (Map<String, Object>) employee.get("address");
            softAssert.assertEquals(address.get("city"), "New York", "City mismatch");
        } else {
            softAssert.fail("Address key is missing in the response!");
        }

        softAssert.assertAll();
    }

    @Test
    public void testExtractAsPojo() {
        NeoEmployeeResponse response = given()
            .when()
            .get("/")
            .then()
            .extract()
            .as(NeoEmployeeResponse.class);

        // Get the first employee from the list
        NeoEmployee neoEmployee = response.getEmployees().get(0);

        // Validate response
        assertEquals(neoEmployee.getFirstName(), "John", "First name should be John");
        assertEquals(neoEmployee.getDepartment(), "IT", "Department should be IT");
        assertEquals(neoEmployee.getId(), 101, "ID should be 101");
        assertEquals(neoEmployee.getEmail(), "john.doe@example.com", "Email should match");
    }

}
