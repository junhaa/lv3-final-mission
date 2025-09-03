package finalmission.domain;

import finalmission.exception.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime time;

    public TimeSlot(Long id, LocalTime time) {
        validateTime(time);
        this.id = id;
        this.time = time;
    }

    public static TimeSlot register(LocalTime time){
        return new TimeSlot(null, time);
    }

    private void validateTime(LocalTime time) {
        if(time == null){
            throw new InvalidInputException("시간은 null일 수 없습니다.");
        }
    }

    protected TimeSlot() {
    }

    public Long getId() {
        return id;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeSlot timeSlot)) {
            return false;
        }
        return Objects.equals(id, timeSlot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
