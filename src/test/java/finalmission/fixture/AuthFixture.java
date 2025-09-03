package finalmission.fixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;

public class AuthFixture {

    public static String GET_USER_TOKEN() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user@email.com", "password", "user"))
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");
    }

    public static String GET_ADMIN_TOKEN() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@email.com", "password", "admin"))
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");
    }

}
