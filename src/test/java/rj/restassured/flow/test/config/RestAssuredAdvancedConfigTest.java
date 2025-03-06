package rj.restassured.flow.test.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.params.CoreConnectionPNames;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.config.DecoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.MultiPartConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;

public class RestAssuredAdvancedConfigTest {
	
	private static final String BASE_URL = "http://localhost:8080/rjrestassuredflowsimple"; // Update with your actual base URL
    private static final String BASE_PATH = "/employees"; 

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL; // Change as needed
        RestAssured.basePath = BASE_PATH;
       
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails())
              //  .connectionConfig(ConnectionConfig.connectionConfig().closeIdleConnectionsAfterEachResponse())
                .decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8"))
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                .redirect(RedirectConfig.redirectConfig().followRedirects(false))
                .multiPartConfig(MultiPartConfig.multiPartConfig().defaultBoundary("----CustomBoundary"))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000) 
                        .setParam("http.socket.timeout", 30000)  
                        .setParam("http.connection-manager.max-total", 50)  
                        .setParam("http.connection-manager.max-per-route", 10))                   
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.GSON));
   
        RestAssured.filters(
                new RequestLoggingFilter(), 
                new ResponseLoggingFilter()
        );
    
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

    String testId;
    
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

        testId =    given()
.contentType(ContentType.JSON)
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

    @Test
    public void testDefaultConfig() {
        Response response = given()
                .when()
                .get()
                .then()
                .contentType(ContentType.JSON)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }

        @Test
        public void testWithCustomConnectionConfig() {
        	       	
            RestAssuredConfig config = RestAssuredConfig.config()
                    .connectionConfig(ConnectionConfig.connectionConfig())
                    .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000) 
                        .setParam("http.socket.timeout", 30000)  
                        .setParam("http.connection-manager.max-total", 50)  
                        .setParam("http.connection-manager.max-per-route", 10)
                    );

            // Make the request with the custom config
            Response response = RestAssured
                    .given()
                //    .baseUri("http://localhost:8080")  // Set base URI for this test
                    .config(config)  // Apply custom configuration
                    .when()
                    .get()  // API endpoint
                    .then()
                    .extract().response();

            // Validate response
            Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        }

    @Test
    public void testWithCustomLogConfig() {
        RestAssuredConfig configWithLogging = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig().enablePrettyPrinting(true));

        Response response = given()
                .config(configWithLogging)
                .log().all()
                .when()
                .get()
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testWithDecoderConfig() {
        RestAssuredConfig configWithDecoder = RestAssuredConfig.config()
                .decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("ISO-8859-1"));

        Response response = given()
                .config(configWithDecoder)
                .when()
                .get()
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testWithSSLConfig() {
        RestAssuredConfig configWithSSL = RestAssuredConfig.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());

        Response response = given()
                .config(configWithSSL)
                .when()
                .get()
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testWithRedirectConfig() {
        RestAssuredConfig configWithRedirect = RestAssuredConfig.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false));

        Response response = given()
                .config(configWithRedirect)
                .when()
                .get()
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testWithObjectMapperConfig() {
        RestAssuredConfig configWithMapper = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.JACKSON_2));

        Response response = given()
                .config(configWithMapper)
                .when()
                .get()
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
