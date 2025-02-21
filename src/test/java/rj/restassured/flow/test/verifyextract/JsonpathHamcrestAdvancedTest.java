package rj.restassured.flow.test.verifyextract;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class JsonpathHamcrestAdvancedTest {
    	final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple";
    	final String BASE_PATH = "/employees/data";
        
        @BeforeClass
    	public void setup() {
    		RestAssured.baseURI = BASE_URL;
    		RestAssured.basePath = BASE_PATH;
    		RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    	}
    

    // ✅ 1️⃣ Boolean Validation (Active Status)
    @Test
    public void testBooleanFieldValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[0].active", is(true))  // Assuming John is active
                .body("employees[1].active", is(false)); // Assuming Jane is inactive
    }

    // ✅ 2️⃣ String Pattern Validation (Regex)
    @Test
    public void testStringPatternValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[0].email", matchesPattern("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$"));

    }

    // ✅ 3️⃣ Validating Employee ID Range
    @Test
    public void testNumberRangeValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[0].id", allOf(greaterThanOrEqualTo(100), lessThan(200)));
    }

    // ✅ 4️⃣ Validating Employees are either in HR or IT department
    @Test
    public void testMultipleConditionsUsingAnyOf() {
        given()
                .when()
                .get()
                .then()
                .body("employees.department", everyItem(anyOf(equalTo("HR"), equalTo("IT"))));
    }

    // ✅ 5️⃣ Checking if any field is null (Negative Case)
    @Test
    public void testNullFieldValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[1].middleName", nullValue()); // Assuming middleName is missing
    }

    // ✅ 6️⃣ Date Format Validation (if applicable)
    @Test
    public void testDateFormatValidation() {
        given()
                .when()
                .get()
                .then()
                .body("employees[0].joiningDate", matchesPattern("^\\d{4}-\\d{2}-\\d{2}$")); // YYYY-MM-DD format
    }

    // ✅ 7️⃣ Advanced findAll() - Filtering employees with age > 30 and active = true
    @Test
    public void testJsonPathFindAllWithMultipleConditions() {
        given()
                .when()
                .get("/")
                .then()
                .body("employees.findAll { it.age > 30 && it.active == true }.size()", greaterThan(0));
    }

    // ✅ 8️⃣ Extracting and Validating Deeply Nested Data
    @Test
    public void testDeeplyNestedData() {
        given()
                .when()
                .get("/")
                .then()
                .body("employees[1].address.city", equalTo("Los Angeles"))
                .body("employees[1].address.zip", matchesPattern("\\d{5}")); // ZIP code pattern
    }
}

