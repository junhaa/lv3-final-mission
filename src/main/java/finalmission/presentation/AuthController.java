package finalmission.presentation;

import finalmission.application.AuthService;
import finalmission.infrastructure.JwtTokenHandler;
import finalmission.presentation.dto.request.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public void login(HttpServletResponse response, @RequestBody LoginRequest request) {
        String token = authService.createToken(request);

        Cookie tokenCookie = new Cookie(JwtTokenHandler.TOKEN_COOKIE_NAME, token);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);

        response.addCookie(tokenCookie);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtTokenHandler.TOKEN_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
