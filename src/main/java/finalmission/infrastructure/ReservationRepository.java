package finalmission.infrastructure;

import finalmission.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"timeSlot"})
    List<Reservation> findByMember_id(Long memberId);

    @EntityGraph(attributePaths = {"timeSlot"})
    List<Reservation> findAll();
}
