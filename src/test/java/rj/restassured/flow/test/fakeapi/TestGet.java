package rj.restassured.flow.test.fakeapi;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.RestAssured.given;

public class TestGet {
	
	public static void main(String[] args) {	
	given()
	    .when()
	    .get("https://jsonplaceholder.typicode.com/posts/1")
	    .then()
	    .statusCode(200)
	    .log().body();
	
}
}

