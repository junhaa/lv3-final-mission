package finalmission.application.request;

public record EmailSendRequest(
    String receiverEmail,
    String title,
    String content
) {
}
