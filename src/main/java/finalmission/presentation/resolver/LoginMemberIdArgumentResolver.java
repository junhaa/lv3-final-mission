package finalmission.presentation.resolver;

import finalmission.global.util.TokenExtractor;
import finalmission.infrastructure.JwtTokenHandler;
import finalmission.presentation.annotation.LoginMemberId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenHandler jwtTokenHandler;
    private final TokenExtractor tokenExtractor;

    public LoginMemberIdArgumentResolver(JwtTokenHandler jwtTokenHandler, TokenExtractor tokenExtractor) {
        this.jwtTokenHandler = jwtTokenHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMemberId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = tokenExtractor.extractTokenByCookies(request, JwtTokenHandler.TOKEN_COOKIE_NAME);

        return jwtTokenHandler.extractTokenInfo(token).memberId();
    }
}
