package rj.restassured.flow.test.fakeapi;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import static io.restassured.RestAssured.given;

public class TestDelete {
	
	public static void main(String[] args) {	
		given()
	    .when()
	    .delete("https://jsonplaceholder.typicode.com/posts/1")
	    .then()
	    .log().all()
	    .statusCode(200);

}
}

