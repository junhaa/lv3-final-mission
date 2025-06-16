package finalmission.presentation.dto;

import finalmission.domain.Member;

public record MemberResponse(
        Long memberId,
        String email,
        String name
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
