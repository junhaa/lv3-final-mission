package finalmission.presentation.dto;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long reservationId,
        LocalDate date,
        LocalTime time,
        Long memberId
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getDate(), reservation.getTime(), reservation.getMemberId());
    }
}
