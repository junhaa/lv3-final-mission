package finalmission.global.config;


import finalmission.presentation.interceptor.LoginRequiredInterceptor;
import finalmission.presentation.resolver.LoginMemberIdArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String ALL_PATH_PATTERN = "/**";
    private static final String CSS_PATH_PATTERN = "/css/**";
    private static final String IMAGE_PATH_PATTERN = "/image/**";
    private static final String JS_PATH_PATTERN = "/js/**";

    private final LoginRequiredInterceptor loginRequiredInterceptor;
    private final LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;

    public WebMvcConfig(
            LoginRequiredInterceptor loginRequiredInterceptor,
            LoginMemberIdArgumentResolver loginMemberIdArgumentResolver
    ) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
        this.loginMemberIdArgumentResolver = loginMemberIdArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns(ALL_PATH_PATTERN)
                .excludePathPatterns(CSS_PATH_PATTERN, IMAGE_PATH_PATTERN, JS_PATH_PATTERN);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberIdArgumentResolver);
    }
}
