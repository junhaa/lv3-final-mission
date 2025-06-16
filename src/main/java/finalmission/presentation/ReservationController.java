package finalmission.presentation;

import finalmission.application.ReservationService;
import finalmission.presentation.annotation.LoginMemberId;
import finalmission.presentation.annotation.LoginRequired;
import finalmission.presentation.dto.ReservationResponse;
import finalmission.presentation.dto.request.ReservationCreateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() {
        return reservationService.getAllReservations();
    }

    @LoginRequired
    @GetMapping("/reservations/login-member")
    public List<ReservationResponse> getMemberReservations(@LoginMemberId Long memberId) {
        return reservationService.getUserReservations(memberId);
    }

    @LoginRequired
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse addReservation(
            @LoginMemberId Long memberId,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        return reservationService.createReservation(memberId, request);
    }

    @LoginRequired
    @DeleteMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@LoginMemberId Long memberId, @PathVariable Long reservationId) {
        reservationService.deleteReservation(memberId, reservationId);
    }
}
