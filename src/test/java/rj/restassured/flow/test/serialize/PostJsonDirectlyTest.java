package rj.restassured.flow.test.serialize;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PostJsonDirectlyTest {

    private static final String BASE_URL = "http://localhost:8080"; // Change if needed

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void testPostComplexJson() {
        // ✅ Define raw JSON payload as a String
        String jsonPayload = """
        {
          "firstName": "Alice",
          "lastName": "Brown",
          "department": "IT",
          "address": {
            "city": "New York",
            "zip": "10001"
          },
          "skills": ["Java", "Spring Boot", "AWS"]
        }
        """;

        // ✅ Send POST request with JSON body
        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonPayload)  // Sending JSON directly
                .when().post("/employees")
                .then().statusCode(201) // Expecting HTTP 201 Created
                .extract().response();

        // ✅ Validate Response
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Alice"));
        Assert.assertTrue(responseBody.contains("Brown"));
        Assert.assertTrue(responseBody.contains("New York"));
        Assert.assertTrue(responseBody.contains("Java"));
    }
}
