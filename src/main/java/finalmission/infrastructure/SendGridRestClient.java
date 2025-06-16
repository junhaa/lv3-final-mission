package finalmission.infrastructure;

import finalmission.application.EmailSenderClient;
import finalmission.application.request.EmailSendRequest;
import finalmission.exception.ExternalApiException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

@Component
public class SendGridRestClient implements EmailSenderClient {

    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String SEND_EMAIL_URI = "/v3/mail/send";
    private static final String MESSAGE_FORMAT = """
            {
              "personalizations": [
                {
                  "to": [{ "email": "%s" }],
                  "subject": "%s"
                }
              ],
              "from": { "email": "%s" },
              "content": [
                {
                  "type": "text/plain",
                  "value": "%s"
                }
              ]
            }
            """;

    private final RestClient restClient;
    private final String senderEmail;

    public SendGridRestClient(
            @Value("${email.sendgrid.apiKey}") String apiKey,
            @Value("${email.sendgrid.senderEmail}") String senderEmail,
            RestClient.Builder builder
    ) {
        restClient = initClient(builder, apiKey);
        this.senderEmail = senderEmail;
    }

    public void sendEmail(EmailSendRequest emailSendRequest) {
        String body = createBody(emailSendRequest);

        restClient.post()
                .uri(SEND_EMAIL_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(
                        status -> status != HttpStatus.ACCEPTED, (request, response) -> {

                            URI uri = request.getURI();

                            throw new ExternalApiException(String.format(
                                    "이메일 전송 API 요청에 실패했습니다. %s",
                                    StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8)
                            ));
                        }
                )
                .toBodilessEntity();
    }

    private String createBody(EmailSendRequest request) {
        // TODO 이메일 서비스 결제 후 실제 이메일로 변경
        return String.format(MESSAGE_FORMAT, "user@example.com", request.title(), senderEmail, request.content());
    }

    private RestClient initClient(RestClient.Builder builder, String apiKey) {
        return builder.defaultHeader(AUTHORIZATION_HEADER, BEARER_HEADER_PREFIX + apiKey)
                .build();
    }
}
