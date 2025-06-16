package finalmission.presentation.interceptor;

import finalmission.global.util.TokenExtractor;
import finalmission.infrastructure.JwtTokenHandler;
import finalmission.presentation.annotation.LoginRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    private final JwtTokenHandler jwtTokenHandler;
    private final TokenExtractor tokenExtractor;

    public LoginRequiredInterceptor(JwtTokenHandler jwtTokenHandler, TokenExtractor tokenExtractor) {
        this.jwtTokenHandler = jwtTokenHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        if (method.getMethodAnnotation(LoginRequired.class) == null &&
                method.getBeanType().getDeclaredAnnotation(LoginRequired.class) == null) {
            return true;
        }

        String token = tokenExtractor.extractTokenByCookies(request, JwtTokenHandler.TOKEN_COOKIE_NAME);
        jwtTokenHandler.validateToken(token);
        return true;
    }
}
