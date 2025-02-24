package rj.restassured.flow.test.objectmapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;

public class GetObjectMapper {
    public static void main(String[] args) {
        // Manually set Jackson 2
        RestAssuredConfig config = RestAssured.config().objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig().defaultObjectMapperType(ObjectMapperType.GSON));

        System.out.println("Explicitly set Object Mapper: " + config.getObjectMapperConfig().defaultObjectMapperType());
    }
}

