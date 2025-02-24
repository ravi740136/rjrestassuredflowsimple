package rj.restassured.flow.test.objectmapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;

public class GetSerializerClass {
    public static void main(String[] args) {
        // Set RestAssured to use Jackson (or any other available)
        RestAssured.config = RestAssured.config().objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.JACKSON_2)
        );

        // Get the ObjectMapper instance
        ObjectMapper objectMapper = RestAssured.config.getObjectMapperConfig().defaultObjectMapper();

        if (objectMapper != null) {
            System.out.println("Serialization class used: " + objectMapper.getClass().getName());
        } else {
            System.out.println("No serialization class found.");
        }
    }
}
