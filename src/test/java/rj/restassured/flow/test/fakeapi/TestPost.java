package rj.restassured.flow.test.fakeapi;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.RestAssured.given;

public class TestPost {
	
	public static void main(String[] args) {	
		given()
	    .contentType("application/json")
	    .body("{\"title\":\"Test\",\"body\":\"This is a test post.\",\"userId\":1}")
	    .when()
	    .post("https://jsonplaceholder.typicode.com/posts")
	    .then()
	    .statusCode(201)
	    .log().body();

	
}
}

