package finalmission.global.util;

import finalmission.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    public String extractTokenByCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            throw new UnauthorizedException("쿠키가 존재하지 않습니다.");
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        throw new UnauthorizedException("쿠키에 인증 토큰이 존재하지 않습니다.");
    }
}
