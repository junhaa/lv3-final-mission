package finalmission.application;

import static finalmission.fixture.MemberFixture.CREATE_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import finalmission.domain.Member;
import finalmission.exception.InvalidInputException;
import finalmission.infrastructure.MemberRepository;
import finalmission.presentation.dto.request.MemberCreateRequest;
import finalmission.presentation.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("사용자를 생성한다.")
    class CreateMember {

        @Test
        @DisplayName("사용자를 정상적으로 생성한다.")
        void createMember() {
            // given
            Member member = CREATE_USER_1();

            MemberCreateRequest request = new MemberCreateRequest(
                    member.getEmail(),
                    member.getPassword(),
                    member.getName()
            );

            when(memberRepository.save(any(Member.class))).thenReturn(member);
            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(false);

            // when
            MemberResponse response = memberService.createMember(request);

            // then
            assertAll(
                    () -> assertThat(response.memberId()).isEqualTo(member.getId()),
                    () -> assertThat(response.name()).isEqualTo(member.getName()),
                    () -> assertThat(response.email()).isEqualTo(member.getEmail()),
                    () -> verify(memberRepository).save(any(Member.class))
            );
        }

        @Test
        @DisplayName("이미 해당하는 이메일의 사용자가 있으면 예외를 던진다.")
        void createMember_WhenEmailDuplicated(){
            // given
            Member member = CREATE_USER_1();

            MemberCreateRequest request = new MemberCreateRequest(
                    member.getEmail(),
                    member.getPassword(),
                    member.getName()
            );

            when(memberRepository.existsByEmail(member.getEmail())).thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> memberService.createMember(request))
                            .isInstanceOf(InvalidInputException.class)
                            .hasMessage("이미 해당 이메일의 사용자가 존재합니다."),
                    () -> verify(memberRepository).existsByEmail(member.getEmail())
            );
        }
    }
}
