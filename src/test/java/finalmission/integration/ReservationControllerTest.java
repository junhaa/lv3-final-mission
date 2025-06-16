package finalmission.integration;

import static finalmission.fixture.AuthFixture.GET_USER_TOKEN;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.presentation.dto.ReservationResponse;
import finalmission.presentation.dto.request.ReservationCreateRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerTest {

    @Nested
    @DisplayName("모든 예약을 조회한다.")
    class GetReservations {

        @Test
        @DisplayName("모든 예약을 정상적으로 조회한다.")
        void getReservations() {
            List<ReservationResponse> responses = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/reservations")
                    .then().statusCode(200)
                    .extract().jsonPath()
                    .getList(".", ReservationResponse.class);

            LocalDate now = LocalDate.now();

            assertAll(
                    () -> assertThat(responses).hasSize(9),
                    () -> assertThat(responses).extracting(ReservationResponse::date)
                            .containsExactlyInAnyOrder(
                                    now.plusDays(2), now.plusDays(2), now.plusDays(3), now.plusDays(2),
                                    now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(3),
                                    now.plusDays(3)
                            ),
                    () -> assertThat(responses).extracting(ReservationResponse::memberId)
                            .containsExactlyInAnyOrder(1L, 1L, 1L, 1L, 2L, 2L, 2L, 2l, 2L)
            );
        }
    }

    @Nested
    @DisplayName("로그인 한 사용자의 예약 목록을 조회한다.")
    class GetMemberReservations {

        @Test
        void getMemberReservations() {
            String token = GET_USER_TOKEN();

            List<ReservationResponse> responses = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .when().get("/reservations/login-member")
                    .then().statusCode(200)
                    .extract().jsonPath()
                    .getList(".", ReservationResponse.class);

            LocalDate now = LocalDate.now();

            assertAll(
                    () -> assertThat(responses).hasSize(4),
                    () -> assertThat(responses).extracting(ReservationResponse::date)
                            .containsExactlyInAnyOrder(
                                    now.plusDays(3), now.plusDays(3), now.plusDays(2), now.plusDays(2)
                            ),
                    () -> assertThat(responses).extracting(ReservationResponse::time)
                            .containsExactlyInAnyOrder(
                                    LocalTime.of(10, 0),
                                    LocalTime.of(11, 0),
                                    LocalTime.of(14, 0),
                                    LocalTime.of(15, 0)
                            )

            );
        }
    }

    @Nested
    @DisplayName("예약을 생성한다.")
    class AddReservation {

        @Test
        @DisplayName("예약을 정상적으로 생성한다.")
        void addReservation() {
            String token = GET_USER_TOKEN();
            LocalDate date = LocalDate.now().plusDays(1);
            Long timeSlotId = 1L;
            ReservationCreateRequest request = new ReservationCreateRequest(date, timeSlotId);

            ReservationResponse response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .body(request)
                    .when().post("/reservations")
                    .then().statusCode(201)
                    .extract().as(ReservationResponse.class);

            assertAll(
                    () -> assertThat(response.date()).isEqualTo(date),
                    () -> assertThat(response.reservationId()).isEqualTo(10L),
                    () -> assertThat(response.time()).isEqualTo(LocalTime.of(10, 0)),
                    () -> assertThat(response.memberId()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("예약을 삭제한다.")
    class DeleteReservation {

        @Test
        @DisplayName("예약을 정상적으로 삭제한다.")
        void deleteReservation() {
            String token = GET_USER_TOKEN();
            Long reservationId = 1L;

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .when().delete("/reservations/{reservationId}", reservationId)
                    .then().statusCode(204);
        }
    }
}
