package finalmission.application;

import finalmission.application.request.EmailSendRequest;

public interface EmailSenderClient {

    void sendEmail(EmailSendRequest request);
}
