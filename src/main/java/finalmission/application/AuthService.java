package finalmission.application;

import finalmission.domain.Member;
import finalmission.exception.UnauthorizedException;
import finalmission.infrastructure.JwtTokenHandler;
import finalmission.infrastructure.MemberRepository;
import finalmission.infrastructure.dto.TokenInfo;
import finalmission.presentation.dto.request.LoginRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final JwtTokenHandler jwtTokenHandler;
    private final MemberRepository memberRepository;

    public AuthService(JwtTokenHandler jwtTokenHandler, MemberRepository memberRepository) {
        this.jwtTokenHandler = jwtTokenHandler;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public String createToken(LoginRequest request){
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("해당 이메일에 대한 사용자가 존재하지 않습니다."));

        if(!member.isPasswordMatch(request.password())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenHandler.issueToken(TokenInfo.from(member));
    }
}
