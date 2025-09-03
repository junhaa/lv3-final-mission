package finalmission.application;

import finalmission.application.request.EmailSendRequest;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.TimeSlot;
import finalmission.exception.ForbiddenException;
import finalmission.exception.NotFoundException;
import finalmission.infrastructure.MemberRepository;
import finalmission.infrastructure.ReservationRepository;
import finalmission.infrastructure.TimeSlotRepository;
import finalmission.presentation.dto.request.ReservationCreateRequest;
import finalmission.presentation.dto.ReservationResponse;
import finalmission.presentation.dto.request.ReservationUpdateRequest;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private static final String CREATE_EMAIL_TITLE = "회의실을 예약이 완료되었습니다!";
    private static final String CREATE_EMAIL_CONTENT_FORMAT = "회의실 %s 예약을 완료하였습니다. 감사합니다.";
    private static final String UPDATE_EMAIL_TITLE = "회의실 예약이 변경되었습니다!";
    private static final String UPDATE_EMAIL_CONTENT_FORMAT = "회의실 %s -> %s 예약을 변경하였습니다. 감사합니다.";
    private static final String DELETE_EMAIL_TITLE = "회의실 예약이 취소되었습니다!";
    private static final String DELETE_EMAIL_CONTENT_FORMAT = "회의실 %s 예약을 취소하였습니다. 감사합니다.";

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final EmailSenderClient emailSenderClient;

    public ReservationService(
            ReservationRepository reservationRepository,
            MemberRepository memberRepository,
            TimeSlotRepository timeSlotRepository,
            EmailSenderClient emailSenderClient
    ) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.emailSenderClient = emailSenderClient;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservations(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundException("존재하지 않는 사용자입니다.");
        }

        return reservationRepository.findByMember_id(memberId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse createReservation(Long memberId, ReservationCreateRequest request) {
        Member member = getMemberOrThrowBy(memberId);
        TimeSlot timeSlot = getTimeSlotOrThrowBy(request.timeSlotId());

        Reservation reservation = Reservation.register(request.date(), timeSlot, member);
        reservationRepository.save(reservation);

        emailSenderClient.sendEmail(createEmailMessage(reservation));

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long memberId, Long reservationId, ReservationUpdateRequest request) {
        Reservation reservation = getReservationById(reservationId);
        validateMemberRole(getMemberOrThrowBy(memberId), reservation.getMemberId());
        LocalTime prevTime = reservation.getTime();

        reservation.updateReservation(request.date(), getTimeSlotOrThrowBy(request.timeSlotId()));

        emailSenderClient.sendEmail(updateEmailMessage(reservation, prevTime));
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId, Long memberId) {
        Member member = getMemberOrThrowBy(memberId);
        Reservation reservation = getReservationById(reservationId);
        validateMemberRole(member, reservation.getMemberId());

        emailSenderClient.sendEmail(deleteEmailMessage(reservation));
        reservationRepository.deleteById(reservation.getId());
    }

    private Member getMemberOrThrowBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private TimeSlot getTimeSlotOrThrowBy(Long slotId) {
        return timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약 입니다."));
    }

    private void validateMemberRole(Member requestMember, Long ownerMemberId) {
        if (!requestMember.canDeleteBy(ownerMemberId)) {
            throw new ForbiddenException(String.format(
                    "예약을 삭제할 권한이 없습니다. requestMemberId: %d, ownerMemberId: %d",
                    requestMember.getId(),
                    ownerMemberId
            ));
        }
    }

    private EmailSendRequest createEmailMessage(Reservation reservation) {
        return new EmailSendRequest(
                reservation.getMemberEmail(),
                CREATE_EMAIL_TITLE,
                String.format(CREATE_EMAIL_CONTENT_FORMAT, reservation.getTime())
        );
    }

    private EmailSendRequest updateEmailMessage(Reservation reservation, LocalTime prevTime) {
        return new EmailSendRequest(
                reservation.getMemberEmail(),
                UPDATE_EMAIL_TITLE,
                String.format(UPDATE_EMAIL_CONTENT_FORMAT, prevTime, reservation.getTime())
        );
    }

    private EmailSendRequest deleteEmailMessage(Reservation reservation) {
        return new EmailSendRequest(
                reservation.getMemberEmail(),
                DELETE_EMAIL_TITLE,
                String.format(DELETE_EMAIL_CONTENT_FORMAT, reservation.getTime())
        );
    }
}
