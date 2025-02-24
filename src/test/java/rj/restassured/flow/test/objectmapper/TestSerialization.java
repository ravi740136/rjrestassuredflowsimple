package rj.restassured.flow.test.objectmapper;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.RestAssured.given;

public class TestSerialization {
    public static void main(String[] args) {
        // Configure RestAssured to use Jackson
       // RestAssured.config = RestAssuredConfig.config()
      //          .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
      //                  .defaultObjectMapperType(ObjectMapperType.JACKSON_2));

        // Sample object
        Employee emp = new Employee(101, "Alice", "Engineering");

        // Send a request to trigger serialization (use a mock/test endpoint)
        given()
                //.config(RestAssured.config)
                .contentType("application/json")
                .body(emp)  // This triggers serialization
                .log().body()  // Logs the JSON body before sending
                .when()
                .post("https://jsonplaceholder.typicode.com/posts") // Fake API
                .then()
                .log().all()
                .statusCode(201);
    }
}

// POJO class
class Employee {
    public int id;
    public String name;
    public String department;

    public Employee(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
