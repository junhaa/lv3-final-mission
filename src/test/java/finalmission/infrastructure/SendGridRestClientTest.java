package finalmission.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import finalmission.application.EmailSenderClient;
import finalmission.application.request.EmailSendRequest;
import finalmission.exception.ExternalApiException;
import finalmission.global.config.RestClientInterceptor;
import finalmission.infrastructure.email.SendGridProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@EnableConfigurationProperties(SendGridProperties.class)
@RestClientTest(SendGridRestClient.class)
class SendGridRestClientTest {

    @Autowired
    EmailSenderClient emailSenderClient;

    @Autowired
    MockRestServiceServer mockServer;

    @Nested
    @DisplayName("이메일 전송 API를 요청한다.")
    class SendEmail {

        @BeforeEach
        void setup() {
            mockServer.reset();
        }

        @Test
        @DisplayName("이메일 전송 API를 정상적으로 요청한다.")
        void sendEmail() {
            // given
            String receiverEmail = "testEmail@email.com";
            String title = "title";
            String content = "content";
            EmailSendRequest request = new EmailSendRequest(receiverEmail, title, content);

            mockServer.expect(requestTo("https://api.sendgrid.com/v3/mail/send")).andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.ACCEPTED));

            // when
            emailSenderClient.sendEmail(request);

            // then
            mockServer.verify();
        }

        @Test
        @DisplayName("이메일 전송 API 응답에서 예외가 발생하면 외부 API 예외를 던진다.")
        void sendEmail_WhenErrorStatus() {
            // given
            String receiverEmail = "testEmail@email.com";
            String title = "title";
            String content = "content";
            EmailSendRequest request = new EmailSendRequest(receiverEmail, title, content);

            mockServer.expect(requestTo("https://api.sendgrid.com/v3/mail/send")).andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.BAD_REQUEST));

            // when & then
            assertAll(
                    () ->assertThatThrownBy(() -> emailSenderClient.sendEmail(request))
                            .isInstanceOf(ExternalApiException.class)
                            .hasMessageContaining("이메일 전송 API 요청에 실패했습니다."),
                    () -> mockServer.verify()
            );
        }
    }

    @TestConfiguration
    public static class SendGridRestClientTestConfig {

        @Bean
        @Qualifier("sendgrid")
        public RestClient.Builder sendGridClientBuilder(
                SendGridProperties properties,
                MockServerRestClientCustomizer mockServerRestClientCustomizer
        ) {

            RestClient.Builder builder = RestClient.builder()
                    .baseUrl(properties.getBaseUrl())
                    .requestInterceptor(new RestClientInterceptor());

            mockServerRestClientCustomizer.customize(builder);

            return builder;
        }
    }
}
