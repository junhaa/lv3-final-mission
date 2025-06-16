package finalmission.infrastructure.dto;

import finalmission.domain.Member;
import finalmission.domain.MemberRole;

public record TokenInfo(
        Long memberId,
        MemberRole role
) {

    public static TokenInfo from(Member member){
        return new TokenInfo(member.getId(), member.getRole());
    }
}
