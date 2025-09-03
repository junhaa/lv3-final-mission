package finalmission.application;

import static finalmission.fixture.MemberFixture.CREATE_ADMIN_1;
import static finalmission.fixture.MemberFixture.CREATE_USER_1;
import static finalmission.fixture.MemberFixture.CREATE_USER_2;
import static finalmission.fixture.ReservationFixture.CREATE_RESERVATION_OF;
import static finalmission.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static finalmission.fixture.TimeSlotFixture.CREATE_TIME_SLOT_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    TimeSlotRepository timeSlotRepository;

    @Mock
    EmailSenderClient emailSenderClient;

    @InjectMocks
    ReservationService reservationService;

    @Nested
    @DisplayName("사용자의 예약 목록을 조회한다")
    class GetUserReservations {

        @Test
        @DisplayName("사용자의 예약 목록을 정상적으로 조회한다.")
        void getUserReservations() {
            // given
            LocalDate date = LocalDate.now();
            TimeSlot time1 = CREATE_TIME_SLOT_1();
            TimeSlot time2 = CREATE_TIME_SLOT_2();
            Member member = CREATE_USER_1();
            Long memberId = member.getId();

            List<Reservation> reservations = List.of(
                    CREATE_RESERVATION_OF(date, time1, member, 1L),
                    CREATE_RESERVATION_OF(date, time2, member, 2L)
            );

            when(memberRepository.existsById(memberId)).thenReturn(true);
            when(reservationRepository.findByMember_id(memberId)).thenReturn(reservations);

            // when
            List<ReservationResponse> userReservations = reservationService.getUserReservations(memberId);

            // then
            assertAll(
                    () -> assertThat(userReservations).hasSize(2),
                    () -> assertThat(userReservations).extracting(ReservationResponse::time)
                            .containsExactlyInAnyOrder(time1.getTime(), time2.getTime()),
                    () -> verify(memberRepository).existsById(memberId),
                    () -> verify(reservationRepository).findByMember_id(memberId)
            );
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외를 던진다.")
        void getUserReservations_WhenMemberNotFound() {
            // given
            Long memberId = 1L;
            when(memberRepository.existsById(1L)).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.getUserReservations(memberId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 사용자입니다."),
                    () -> verify(memberRepository).existsById(1L)
            );
        }
    }

    @Nested
    @DisplayName("전체 예약 목록을 조회한다.")
    class GetAllReservations {

        @Test
        @DisplayName("전체 예약 목록을 성공적으로 조회한다.")
        void getAllReservations() {
            // given
            LocalDate date1 = LocalDate.now();
            LocalDate date2 = LocalDate.now().plusDays(1);
            TimeSlot time1 = CREATE_TIME_SLOT_1();
            TimeSlot time2 = CREATE_TIME_SLOT_2();
            Member member1 = CREATE_USER_1();
            Member member2 = CREATE_USER_2();

            List<Reservation> reservations = List.of(
                    CREATE_RESERVATION_OF(date1, time1, member1, 1L),
                    CREATE_RESERVATION_OF(date1, time2, member2, 2L),
                    CREATE_RESERVATION_OF(date2, time1, member2, 3L),
                    CREATE_RESERVATION_OF(date2, time2, member1, 4L)
            );

            when(reservationRepository.findAll()).thenReturn(reservations);

            // when
            List<ReservationResponse> responses = reservationService.getAllReservations();

            // then
            assertAll(
                    () -> assertThat(responses).hasSize(4),
                    () -> assertThat(responses).extracting(ReservationResponse::reservationId)
                            .containsExactlyInAnyOrder(1L, 2L, 3L, 4L),
                    () -> assertThat(responses).extracting(ReservationResponse::memberId)
                            .containsExactlyInAnyOrder(1L, 2L, 2L, 1L),
                    () -> verify(reservationRepository).findAll()
            );
        }
    }

    @Nested
    @DisplayName("예약을 생성한다.")
    class CreateReservation {

        @Test
        @DisplayName("예약을 정상적으로 생성한다.")
        void createReservation() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot time = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            ReservationCreateRequest request = new ReservationCreateRequest(date, time.getId());

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(timeSlotRepository.findById(time.getId())).thenReturn(Optional.of(time));

            // when
            ReservationResponse response = reservationService.createReservation(member.getId(), request);

            // then
            assertAll(
                    () -> assertThat(response.date()).isEqualTo(date),
                    () -> assertThat(response.time()).isEqualTo(time.getTime()),
                    () -> assertThat(response.memberId()).isEqualTo(member.getId()),
                    () -> verify(memberRepository).findById(member.getId()),
                    () -> verify(timeSlotRepository).findById(time.getId()),
                    () -> verify(emailSenderClient).sendEmail(any(EmailSendRequest.class))
            );
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외를 던진다.")
        void createReservation_WhenMemberNotFound() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot time = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            ReservationCreateRequest request = new ReservationCreateRequest(date, time.getId());

            when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 사용자입니다."),
                    () -> verify(memberRepository).findById(member.getId())
            );
        }

        @Test
        @DisplayName("예약 시간이 존재하지 않으면 예외를 던진다.")
        void createReservation_WhenTimeSlotNotFound() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot time = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            ReservationCreateRequest request = new ReservationCreateRequest(date, time.getId());

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(timeSlotRepository.findById(time.getId())).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 예약 시간입니다."),
                    () -> verify(memberRepository).findById(member.getId()),
                    () -> verify(timeSlotRepository).findById(time.getId())
            );
        }
    }

    @Nested
    @DisplayName("예약 정보를 수정한다.")
    class UpdateReservation {

        @Test
        @DisplayName("예약 정보를 정상적으로 수정한다.")
        void updateReservation() {
            // given
            Long reservationId = 1L;
            LocalDate date = LocalDate.now().plusDays(1);
            Member member = CREATE_USER_1();
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Reservation reservation = CREATE_RESERVATION_OF(date, timeSlot, member, 1L);

            LocalDate updateDate = LocalDate.now().plusDays(2);
            TimeSlot updateTimeSlot = CREATE_TIME_SLOT_2();
            Reservation updateReservation = CREATE_RESERVATION_OF(updateDate, updateTimeSlot, member, 1L);

            ReservationUpdateRequest request = new ReservationUpdateRequest(updateTimeSlot.getId(), updateDate);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(timeSlotRepository.findById(updateTimeSlot.getId())).thenReturn(Optional.of(updateTimeSlot));

            // when
            ReservationResponse response = reservationService.updateReservation(
                    member.getId(),
                    reservationId,
                    request
            );

            // then
            ReservationResponse expected = new ReservationResponse(
                    updateReservation.getId(),
                    updateDate,
                    updateReservation.getTime(),
                    updateReservation.getMemberId()
            );
            assertAll(
                    () -> assertThat(response).isEqualTo(expected),
                    () -> verify(timeSlotRepository).findById(updateTimeSlot.getId()),
                    () -> verify(reservationRepository).findById(reservationId),
                    () -> verify(emailSenderClient).sendEmail(any(EmailSendRequest.class))
            );
        }

        @Test
        @DisplayName("예약이 존재하지 않으면 예외를 던진다.")
        void updateReservation_WhenReservationNotFound() {
            // given
            Long reservationId = 1L;
            Member member = CREATE_USER_1();
            ReservationUpdateRequest request = mock(ReservationUpdateRequest.class);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.updateReservation(
                            member.getId(),
                            reservationId,
                            request
                    )),
                    () -> verify(reservationRepository).findById(reservationId)
            );
        }
    }

    @Nested
    @DisplayName("예약 정보를 삭제한다.")
    class DeleteReservation {

        @Test
        @DisplayName("예약 정보를 정상적으로 삭제한다.")
        void deleteReservation() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            Reservation reservation = CREATE_RESERVATION_OF(date, timeSlot, member, 1L);

            when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
            when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

            // when
            reservationService.deleteReservation(reservation.getId(), member.getId());

            // then
            assertAll(
                    () -> verify(memberRepository).findById(member.getId()),
                    () -> verify(reservationRepository).findById(reservation.getId()),
                    () -> verify(reservationRepository).deleteById(reservation.getId()),
                    () -> verify(emailSenderClient).sendEmail(any(EmailSendRequest.class))
            );
        }

        @Test
        @DisplayName("관리자는 예약 정보를 정상적으로 삭제한다.")
        void deleteReservation_WhenAdminDelete() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            Reservation reservation = CREATE_RESERVATION_OF(date, timeSlot, member, 1L);

            Member admin = CREATE_ADMIN_1();

            when(memberRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
            when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

            // when
            reservationService.deleteReservation(reservation.getId(), admin.getId());

            // then
            assertAll(
                    () -> verify(memberRepository).findById(admin.getId()),
                    () -> verify(reservationRepository).findById(reservation.getId()),
                    () -> verify(reservationRepository).deleteById(reservation.getId())
            );
        }

        @Test
        @DisplayName("삭제 권한이 없으면 예외를 던진다.")
        void deleteReservation_WhenInvalidUserDelete() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Member member = CREATE_USER_1();

            Reservation reservation = CREATE_RESERVATION_OF(date, timeSlot, member, 1L);

            Member invalidUser = CREATE_USER_2();

            when(memberRepository.findById(invalidUser.getId())).thenReturn(Optional.of(invalidUser));
            when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.deleteReservation(
                            reservation.getId(),
                            invalidUser.getId()
                    ))
                            .isInstanceOf(ForbiddenException.class)
                            .hasMessageContaining("예약을 삭제할 권한이 없습니다."),
                    () -> verify(memberRepository).findById(invalidUser.getId()),
                    () -> verify(reservationRepository).findById(reservation.getId())
            );
        }
    }
}
