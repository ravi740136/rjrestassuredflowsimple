package rj.restassured.flow.test.serialize;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import rj.restassured.flow.entity.Employee;
import rj.restassured.flow.entity.NeoEmployee;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class EmployeeControllerDeSerializeTest {

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

    /**
     * ✅ GET Employee by ID using response.as()
     */
    @Test
    public void testGetEmployeeObject_Deserialization() {
        Response response = given()
            .pathParam("id", testid)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .extract().response();

        Employee employee = response.as(Employee.class);
        Assert.assertNotNull(employee);
        //Assert.assertEquals(employee.getId(), Long.valueOf(101));
        Assert.assertEquals(employee.getFirstName(), "John");
    }

    /**
     * ✅ GET all Employees as List<Employee>
     */
    @Test
    public void testGetAllEmployees_AsList() {
        Response response = when()
            .get()
        .then()
            .statusCode(200)
            .extract().response();

        List<Employee> employees = response.as(new TypeRef<List<Employee>>() {});
        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(employees.get(0).getFirstName(), "John");
    }
    
    @Test
    public void testGetAllEmployees_AsArray() {
        Response response = when()
            .get()
        .then()
            .statusCode(200)
            .extract().response();

        Employee[] employees = response.as(Employee[].class);
        Assert.assertTrue(employees.length > 0);
        Assert.assertEquals(employees[0].getFirstName(), "John");
    }

    /**
     * ✅ GET Employee Name using response.path()
     */
    @Test
    public void testGetEmployeeNameUsingPath() {
        String firstName = given()
            .pathParam("id", testid)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .extract().path("firstName");

        Assert.assertEquals(firstName, "John");
    }

    /**
     * ✅ GET Employee Department using response.jsonPath()
     */
    @Test
    public void testGetEmployeeDepartmentUsingJsonPath() {
        Response response = given()
            .pathParam("id", testid)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String department = jsonPath.getString("department");
        Assert.assertEquals(department, "IT");
    }

    /**
     * ✅ GET Employee JSON Object using jsonPath().getMap()
     */
    @Test
    public void testGetEmployeeAsMap() {
        Response response = given()
            .pathParam("id", testid)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        //Map<String, Object> employeeMap = jsonPath.get("$");
      //  Map<String, String> employeeMap = jsonPath.getMap("$", String.class, String.class);
        //here all will work same way
        Map<String, Object> employeeMap = jsonPath.getMap("$");
//without dollar also it will work
       // Map<String, Object> employeeMap = jsonPath.getMap("");
        
        Assert.assertEquals(employeeMap.get("firstName"), "John");
        Assert.assertEquals(employeeMap.get("department"), "IT");
    }

    /**
     * ✅ POST Search Employees By Department & City
     */
    @Test
    public void testSearchEmployeesasListTyperef() {
        Response response = given()
            .queryParam("department", "IT")
            .queryParam("city", "New York")
        .when()
            .post("/search")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .extract().response();

        if (response.statusCode() == 200) {
        
            List<Employee> employees = response.as(new TypeRef<List<Employee>>() {});
        	System.out.println("employees found "+employees);
            Assert.assertFalse(employees.isEmpty());
            Assert.assertEquals(employees.get(0).getCity(), "New York");
        }
    }

    /**
     * ✅ GET Employee By First & Last Name
     */
    @Test
    public void testGetEmployeeByName() {
        Response response = given()
            .queryParam("firstName", "John")
            .queryParam("lastName", "Doe")
        .when()
            .get("/search/name")
        .then()
            .statusCode(anyOf(is(200), is(204)))
            .extract().response();

        if (response.statusCode() == 200) {
            Employee employee = response.as(Employee.class);
            Assert.assertEquals(employee.getFirstName(), "John");
            Assert.assertEquals(employee.getLastName(), "Doe");
        }
    }

    /**
     * ✅ GET Employee Data using jsonPath().getList()
     */
    @Test
    public void testGetEmployeeData_AsJsonpathListOfMap() {
        Response response = given()
        .when()
            .get("/data")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> employees = jsonPath.getList("employees");

        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(employees.get(0).get("firstName"), "John");
    }

    /**
     * ✅ GET Employee Data using response.as(new TypeRef<List<Employee>>())
     */
    @Test
    public void testGetEmployeeData_AsJsonpathEmployeeList() {
        Response response = given()
        .when()
            .get("/data")
        .then()
            .statusCode(200)
            .extract().response();

        List<NeoEmployee> employees = response.jsonPath().getList("employees", NeoEmployee.class);
        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(employees.get(0).getFirstName(), "John");
    }

   
    
   

    /**
     * ✅ GET Employee Names List using JSONPath
     */
    @Test
    public void testGetEmployeeNamesListUsingJsonPath() {
        Response response = given()
        .when()
            .get("/data")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> firstNames = jsonPath.getList("employees.firstName");

        Assert.assertFalse(firstNames.isEmpty());
        Assert.assertTrue(firstNames.contains("John"));
    }

    /**
     * ✅ Extracting Nested JSON Fields (e.g., Address City)
     */
    @Test
    public void testGetEmbedded_EmployeeAddressCity() {
        Response response = given()
        .when()
            .get("/data")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> cities = jsonPath.getList("employees.address.city");

        Assert.assertFalse(cities.isEmpty());
        Assert.assertTrue(cities.contains("New York"));
    }

    /**
     * ✅ Extract Active Employees Using JSONPath Condition
     */
    @Test
    public void testGetActiveEmployees_listfindall() {
        Response response = given()
        .when()
            .get("/data")
        .then()
            .statusCode(200)
            .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> activeEmployees = jsonPath.getList("employees.findAll { it.active == true }");

        Assert.assertFalse(activeEmployees.isEmpty());
    }

    
    @Test
    public void testExtractIntValue() {
        Response response = given()
                .when().get("/data")
                .then().statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        int age = jsonPath.getInt("employees[0].age");  // Extracting as int

        Assert.assertEquals(age, 35, "Employee age should match");
    }

    @Test
    public void testExtractBooleanValue() {
        Response response = given()
                .when().get( "/data")
                .then().statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        boolean isActive = jsonPath.getBoolean("employees[0].active");  // Extracting as boolean

        Assert.assertTrue(isActive, "Employee should be active");
    }

    @Test
    public void testExtractDoubleValue() {
        Response response = given()
                .when().get("/data")
                .then().statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        double salary = jsonPath.getDouble("employees[0].salary");  // Extracting as double

        Assert.assertEquals(salary, 75000.0, "Employee salary should match");
    }
}
