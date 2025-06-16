package finalmission.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {

    @Nested
    @DisplayName("로그인을 진행한다.")
    class Login {

        @CsvSource({
                "user@email.com, user",
                "admin@email.com, admin"
        })
        @ParameterizedTest
        @DisplayName("정상적으로 로그인을 진행한다.")
        void login(String email, String password) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", email, "password", password))
                    .when().post("/auth/login")
                    .then().statusCode(200)
                    .extract().response().getDetailedCookies().hasCookieWithName("token");
        }

        @Test
        @DisplayName("이메일에 대한 사용자가 존재하지 않으면 401예외를 던진다.")
        void login_WhenNotFoundMember() {
            String email = "invalidUser@email.com";
            String password = "user";

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", email, "password", password))
                    .when().post("/auth/login")
                    .then().statusCode(401);
        }

        @Test
        @DisplayName("비밀번호가 올바르지 않으면 401예외를 던진다.")
        void login_WhenPasswordNotMatch() {
            String email = "user@email.com";
            String password = "invalidPassword";

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", email, "password", password))
                    .when().post("/auth/login")
                    .then().statusCode(401);
        }
    }

}
