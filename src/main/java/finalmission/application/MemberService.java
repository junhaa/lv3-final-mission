package finalmission.application;

import finalmission.domain.Member;
import finalmission.exception.InvalidInputException;
import finalmission.infrastructure.MemberRepository;
import finalmission.presentation.dto.request.MemberCreateRequest;
import finalmission.presentation.dto.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request){
        validateDuplicateEmail(request.email());
        Member member = memberRepository.save(Member.register(request.email(), request.password(), request.name()));

        return MemberResponse.from(member);
    }

    private void validateDuplicateEmail(String email){
        if(memberRepository.existsByEmail(email)){
            throw new InvalidInputException("이미 해당 이메일의 사용자가 존재합니다.");
        }
    }

}
