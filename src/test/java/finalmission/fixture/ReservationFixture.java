package finalmission.fixture;

import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.TimeSlot;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;

public class ReservationFixture {

    public static Reservation CREATE_RESERVATION_OF(LocalDate date, TimeSlot timeSlot, Member member, Long id) {
        Reservation register = Reservation.register(date, timeSlot, member);

        ReflectionTestUtils.setField(register, "id", id);
        return register;
    }

    public static Reservation CREATE_RESERVATION_OF(LocalDate date, TimeSlot timeSlot, Member member) {
        return CREATE_RESERVATION_OF(date, timeSlot, member, 1L);
    }
}
