package finalmission.global.config;

import finalmission.infrastructure.email.SendGridProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("sendgrid")
    public RestClient.Builder sendGridClientBuilder(SendGridProperties properties, RestClientInterceptor interceptor) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestInterceptor(interceptor)
                .requestFactory(createRequestFactory(properties.getConnectTimeout(), properties.getReadTimeout()));
    }

    @Bean
    public RestClientInterceptor restClientInterceptor() {
        return new RestClientInterceptor();
    }

    private ClientHttpRequestFactory createRequestFactory(Integer connectionTimeout, Integer readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if(connectionTimeout == null || readTimeout == null) {
            throw new IllegalStateException("타임아웃 값이 올바르지 않습니다.");
        }

        return requestFactory;
    }

}

