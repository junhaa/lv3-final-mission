package finalmission.application;

import static finalmission.fixture.MemberFixture.CREATE_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import finalmission.domain.Member;
import finalmission.exception.UnauthorizedException;
import finalmission.infrastructure.JwtTokenHandler;
import finalmission.infrastructure.MemberRepository;
import finalmission.infrastructure.dto.TokenInfo;
import finalmission.presentation.dto.request.LoginRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    JwtTokenHandler jwtTokenHandler;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AuthService authService;

    @Nested
    @DisplayName("인증 토큰을 생성한다.")
    class CreateToken{

        @Test
        @DisplayName("인증 토큰을 정상적으로 생성한다.")
        void createToken(){
            // given
            Member member = CREATE_USER_1();
            LoginRequest request = new LoginRequest(member.getEmail(), member.getPassword());
            String token = "token";

            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
            when(jwtTokenHandler.issueToken(any(TokenInfo.class))).thenReturn(token);

            // when
            String issuedToken = authService.createToken(request);

            // then
            assertAll(
                    () -> assertThat(issuedToken).isEqualTo(token),
                    () -> verify(memberRepository).findByEmail(member.getEmail()),
                    () -> verify(jwtTokenHandler).issueToken(any(TokenInfo.class))
            );
        }

        @Test
        @DisplayName("이메일에 해당하는 사용자가 존재하지 않으면 예외가 발생한다.")
        void createToken_WhenEmailInvalid(){
            // given
            Member member = CREATE_USER_1();
            String email = "invalid@email.com";
            LoginRequest request = new LoginRequest(email, member.getPassword());

            when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authService.createToken(request))
                            .isInstanceOf(UnauthorizedException.class)
                            .hasMessage("해당 이메일에 대한 사용자가 존재하지 않습니다."),
                    () -> verify(memberRepository).findByEmail(email)
            );
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
        void createToken_WhenPasswordNotMatches(){
            // given
            Member member = CREATE_USER_1();
            String password = "invalidPassword";
            LoginRequest request = new LoginRequest(member.getEmail(), password);

            when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authService.createToken(request))
                            .isInstanceOf(UnauthorizedException.class)
                            .hasMessage("비밀번호가 일치하지 않습니다."),
                    () -> verify(memberRepository).findByEmail(member.getEmail())
            );
        }
    }
}
