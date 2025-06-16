package finalmission.global.config;

import finalmission.exception.ExternalApiException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        try {
            return execution.execute(request, body);
        } catch (ConnectException e) {
            throw new ExternalApiException("외부 API 서버에 연결할 수 없습니다.");
        } catch (SocketTimeoutException e) {
            throw new ExternalApiException("외부 API 응답 시간이 초과되었습니다");
        } catch (Exception e) {
            throw new ExternalApiException("외부 API와 통신 중 오류가 발생했습니다.", e.getCause());
        }
    }
}
