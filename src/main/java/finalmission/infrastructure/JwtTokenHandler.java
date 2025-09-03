package finalmission.infrastructure;

import finalmission.domain.MemberRole;
import finalmission.exception.UnauthorizedException;
import finalmission.infrastructure.dto.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHandler {

    public static final String MEMBER_ROLE_CLAIM_KEY = "role";
    public static final String TOKEN_COOKIE_NAME = "token";


    private final String secretKey;
    private final Long expireLength;

    public JwtTokenHandler(
            @Value("${security.jwt.token.secretKey}") String secretKey,
            @Value("${security.jwt.token.expireLength}") Long expireLength
    ) {
        this.secretKey = secretKey;
        this.expireLength = expireLength;
    }

    public String issueToken(TokenInfo tokenInfo) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setSubject(tokenInfo.memberId().toString())
                .claim(MEMBER_ROLE_CLAIM_KEY, tokenInfo.role())
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            parseAndValidateClaims(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다: " + e.getMessage());
        }
    }

    public TokenInfo extractTokenInfo(String token) {
        Claims body = parseAndValidateClaims(token);
        return new TokenInfo(Long.parseLong(body.getSubject()), 
                         MemberRole.valueOf(body.get(MEMBER_ROLE_CLAIM_KEY, String.class)));
    }

    private Claims parseAndValidateClaims(String token) {
        Claims body = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    
    Long memberId = Long.parseLong(body.getSubject());
    MemberRole memberRole = MemberRole.valueOf(body.get(MEMBER_ROLE_CLAIM_KEY, String.class));
    
    validateMemberId(memberId);
    validateMemberRole(memberRole);
    return body;
}


    private void validateMemberId(Long memberId) {
        if(memberId == null){
            throw new UnauthorizedException("인증 토큰 내부의 사용자의 ID값이 올바르지 않습니다.");
        }
    }

    private void validateMemberRole(MemberRole memberRole) {
        if(memberRole == null){
            throw new UnauthorizedException("인증 토큰 내부의 사용자의 권한이 올바르지 않습니다.");
        }
    }
}
