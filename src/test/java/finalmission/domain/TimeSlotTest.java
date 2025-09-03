package finalmission.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.exception.InvalidInputException;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TimeSlotTest {

    @Nested
    @DisplayName("id가 null인 예약 시간 객체를 생성한다.")
    class Register {

        @Test
        @DisplayName("정상적으로 생성한다.")
        void register() {
            // given
            LocalTime time = LocalTime.of(10, 0);

            // when
            TimeSlot register = TimeSlot.register(time);

            // then
            assertAll(
                    () -> assertThat(register.getTime()).isEqualTo(time),
                    () -> assertThat(register.getId()).isNull()
            );
        }

        @Test
        @DisplayName("시간이 null이면 예외를 던진다.")
        void register_WhenTimeNull() {
            // when & then
            assertThatThrownBy(() -> TimeSlot.register(null))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("시간은 null일 수 없습니다.");
        }
    }
}
