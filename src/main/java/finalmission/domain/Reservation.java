package finalmission.domain;

import finalmission.exception.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    public Reservation(Long id, LocalDate date, TimeSlot timeSlot, Member member) {
        validateDate(date);
        validateTimeSlot(timeSlot);
        validateMember(member);
        this.id = id;
        this.date = date;
        this.timeSlot = timeSlot;
        this.member = member;
    }

    public static Reservation register(LocalDate date, TimeSlot timeSlot, Member member) {
        return new Reservation(null, date, timeSlot, member);
    }

    private void validateDate(LocalDate date) {
        if(date == null){
            throw new InvalidInputException("예약 날짜는 null일 수 없습니다.");
        }
    }

    private void validateTimeSlot(TimeSlot timeSlot) {
        if(timeSlot == null){
            throw new InvalidInputException("예약 시간은 null일 수 없습니다.");
        }
    }

    private void validateMember(Member member) {
        if(member == null){
            throw new InvalidInputException("예약자는 null일 수 없습니다.");
        }
    }

    protected Reservation() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return timeSlot.getTime();
    }

    public Long getMemberId() {
        return member.getId();
    }

    public String getMemberEmail(){
        return member.getEmail();
    }

    public void updateReservation(LocalDate date, TimeSlot timeSlot) {
        this.date = date;
        this.timeSlot = timeSlot;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Reservation that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
