package com.company.meetingroom.service;

import com.company.meetingroom.dto.ReservationRequest;
import com.company.meetingroom.entity.MeetingRoom;
import com.company.meetingroom.entity.Reservation;
import com.company.meetingroom.entity.User;
import com.company.meetingroom.exception.BusinessException;
import com.company.meetingroom.repository.MeetingRoomRepository;
import com.company.meetingroom.repository.ReservationRepository;
import com.company.meetingroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation createReservation(Long userId, ReservationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("不能预约过去的时间");
        }

        MeetingRoom room = meetingRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BusinessException("会议室不存在"));
        if (room.getStatus() != 1) {
            throw new BusinessException("该会议室当前不可用");
        }

        List<Reservation> conflicts = reservationRepository.findConflicts(
                request.getRoomId(), request.getStartTime(), request.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new BusinessException("该时间段与已有预约冲突，请选择其他时间");
        }

        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setUserId(userId);
        reservation.setTitle(request.getTitle());
        reservation.setDescription(request.getDescription());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("PENDING");

        Reservation saved = reservationRepository.save(reservation);
        enrichReservation(saved);
        return saved;
    }

    public List<Reservation> getMyReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public List<Reservation> getPendingReservations() {
        List<Reservation> reservations = reservationRepository.findByStatusOrderByStartTimeDesc("PENDING");
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public List<Reservation> getRoomReservations(Long roomId) {
        List<Reservation> reservations = reservationRepository.findByRoomIdOrderByStartTimeDesc(roomId);
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public List<Reservation> getRoomReservationsByDate(Long roomId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Reservation> reservations = reservationRepository.findByRoomAndDate(roomId, startOfDay, endOfDay);
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    @Transactional
    public Reservation approveReservation(Long reservationId, Long adminId) {
        Reservation reservation = getReservationById(reservationId);
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BusinessException("只能审批待审批的预约");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        if (!"ADMIN".equals(admin.getRole())) {
            throw new BusinessException("只有管理员才能审批预约");
        }

        List<Reservation> conflicts = reservationRepository.findConflictsExcludeSelf(
                reservation.getRoomId(), reservationId,
                reservation.getStartTime(), reservation.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new BusinessException("审批失败：该时间段存在冲突的预约");
        }

        reservation.setStatus("APPROVED");
        reservation.setApproveUserId(adminId);
        reservation.setApproveTime(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);
        enrichReservation(saved);
        return saved;
    }

    @Transactional
    public Reservation rejectReservation(Long reservationId, Long adminId) {
        Reservation reservation = getReservationById(reservationId);
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BusinessException("只能拒绝待审批的预约");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        if (!"ADMIN".equals(admin.getRole())) {
            throw new BusinessException("只有管理员才能拒绝预约");
        }

        reservation.setStatus("REJECTED");
        reservation.setApproveUserId(adminId);
        reservation.setApproveTime(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);
        enrichReservation(saved);
        return saved;
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId, String cancelReason) {
        Reservation reservation = getReservationById(reservationId);
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("只能取消自己的预约");
        }
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new BusinessException("预约已取消");
        }
        if ("REJECTED".equals(reservation.getStatus())) {
            throw new BusinessException("预约已被拒绝，无需取消");
        }

        reservation.setStatus("CANCELLED");
        reservation.setCancelReason(cancelReason);

        Reservation saved = reservationRepository.save(reservation);
        enrichReservation(saved);
        return saved;
    }

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("预约不存在"));
    }

    private void enrichReservation(Reservation reservation) {
        meetingRoomRepository.findById(reservation.getRoomId())
                .ifPresent(room -> reservation.setRoomName(room.getName()));
        userRepository.findById(reservation.getUserId())
                .ifPresent(user -> reservation.setUserName(user.getRealName()));
        if (reservation.getApproveUserId() != null) {
            userRepository.findById(reservation.getApproveUserId())
                    .ifPresent(admin -> reservation.setApproveUserName(admin.getRealName()));
        }
    }
}
