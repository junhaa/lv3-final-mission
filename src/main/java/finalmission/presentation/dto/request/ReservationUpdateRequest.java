package finalmission.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationUpdateRequest(
        @NotNull(message = "예약 시간 ID를 입력해주세요.")
        Long timeSlotId,

        @NotNull(message = "예약 날짜를 입력해주세요.")
        LocalDate date
) {
}
