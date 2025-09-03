package finalmission.integration;

import static org.hamcrest.Matchers.equalTo;

import finalmission.presentation.dto.request.MemberCreateRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberControllerTest {

    @Nested
    @DisplayName("사용자를 생성한다.")
    class CreateMember {

        @Test
        @DisplayName("사용자를 정상적으로 생성한다.")
        void createMember() {
            String email = "createUser@email.com";
            String password = "createUser";
            String name = "createUser";

            MemberCreateRequest request = new MemberCreateRequest(email, password, name);

            RestAssured.given()
                    .contentType("application/json")
                    .body(request)
                    .when().post("/members")
                    .then().statusCode(201)
                    .body("memberId", equalTo(3))
                    .body("email", equalTo(email))
                    .body("name", equalTo(name));
        }

        @Test
        @DisplayName("이메일이 중복되는 경우 400 응답코드를 반환한다.")
        void createMember_WhenEmailDuplicated() {
            String email = "user@email.com";
            String password = "createUser";
            String name = "createUser";

            MemberCreateRequest request = new MemberCreateRequest(email, password, name);

            RestAssured.given()
                    .contentType("application/json")
                    .body(request)
                    .when().post("/members")
                    .then().statusCode(400);
        }
    }
}
