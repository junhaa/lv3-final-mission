package finalmission.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import finalmission.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MemberTest {

    @Nested
    @DisplayName("id가 null인 사용자 객체를 생성한다.")
    class Register {

        @Test
        @DisplayName("정상적으로 생성한다.")
        void register() {
            // given
            String email = "test@email.com";
            String password = "password";
            String name = "test";

            // when
            Member register = Member.register(email, password, name);

            // then
            assertAll(
                    () -> assertThat(register.getEmail()).isEqualTo(email),
                    () -> assertThat(register.getName()).isEqualTo(name),
                    () -> assertThat(register.getPassword()).isEqualTo(password),
                    () -> assertThat(register.getId()).isNull()
            );
        }

        @NullAndEmptySource
        @ParameterizedTest
        @DisplayName("이메일이 null이거나 공백이면 예외를 던진다.")
        void register_WhenEmailNullOrEmpty(String email) {
            // given
            String password = "password";
            String name = "test";

            // when & then
            assertThatThrownBy(() -> Member.register(email, password, name))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("이메일은 null이거나 공백일 수 없습니다.");
        }

        @NullAndEmptySource
        @ParameterizedTest
        @DisplayName("비밀번호가 null이거나 공백이면 예외를 던진다.")
        void register_WhenPasswordNullOrEmpty(String password) {
            // given
            String email = "test@email.com";
            String name = "test";

            // when & then
            assertThatThrownBy(() -> Member.register(email, password, name))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("비밀번호는 null이거나 공백일 수 없습니다.");
        }

        @NullAndEmptySource
        @ParameterizedTest
        @DisplayName("이름이 null이거나 공백이면 예외를 던진다.")
        void register_WhenNameNullOrEmpty(String name) {
            // given
            String email = "test@email.com";
            String password = "password";

            // when & then
            assertThatThrownBy(() -> Member.register(email, password, name))
                    .isInstanceOf(InvalidInputException.class)
                    .hasMessage("이름은 null이거나 공백일 수 없습니다.");
        }
    }
}
