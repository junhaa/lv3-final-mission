package finalmission.domain;

import static finalmission.fixture.MemberFixture.CREATE_USER_1;
import static finalmission.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.exception.InvalidInputException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Nested
    @DisplayName("id가 null인 예약 객체를 생성한다.")
    class Register {

        @Test
        @DisplayName("정상적으로 생성한다.")
        void register() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            // when
            Reservation register = Reservation.register(date, timeSlot, member);

            // then
            assertAll(
                    () -> assertThat(register.getDate()).isEqualTo(date),
                    () -> assertThat(register.getTime()).isEqualTo(timeSlot.getTime()),
                    () -> assertThat(register.getMemberId()).isEqualTo(member.getId()),
                    () -> assertThat(register.getId()).isNull()
            );
        }

        @Test
        @DisplayName("예약 날짜가 null이면 예외를 던진다.")
        void register_WhenDateNull() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            // when & then
            assertThatThrownBy(() -> Reservation.register(null, timeSlot, member))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("예약 날짜는 null일 수 없습니다.");
        }

        @Test
        @DisplayName("예약 시간가 null이면 예외를 던진다.")
        void register_WhenTimeSlotNull() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            Member member = CREATE_USER_1();

            // when & then
            assertThatThrownBy(() -> Reservation.register(date, null, member))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("예약 시간은 null일 수 없습니다.");
        }

        @Test
        @DisplayName("예약자가 null이면 예외를 던진다.")
        void register_WhenMemberNull() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();

            // when & then
            assertThatThrownBy(() -> Reservation.register(date, timeSlot, null))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("예약자는 null일 수 없습니다.");
        }
    }
}
