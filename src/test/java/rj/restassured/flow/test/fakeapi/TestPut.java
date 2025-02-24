package rj.restassured.flow.test.fakeapi;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.RestAssured.given;

public class TestPut {
	
	public static void main(String[] args) {	
		given()
	    .contentType("application/json")
	    .body("{\"title\":\"Updated Title\"}")
	    .when()
	    .put("https://jsonplaceholder.typicode.com/posts/1")
	    .then()
	    .statusCode(200)
	    .log().body();	
}
}

