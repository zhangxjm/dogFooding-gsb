package com.company.meetingroom.controller;

import com.company.meetingroom.dto.ApiResponse;
import com.company.meetingroom.dto.ReservationRequest;
import com.company.meetingroom.entity.Reservation;
import com.company.meetingroom.service.ReservationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ApiResponse<Reservation> createReservation(@Valid @RequestBody ReservationRequest request,
                                                       HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success("预约成功", reservationService.createReservation(userId, request));
    }

    @GetMapping("/my")
    public ApiResponse<List<Reservation>> getMyReservations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success(reservationService.getMyReservations(userId));
    }

    @GetMapping("/pending")
    public ApiResponse<List<Reservation>> getPendingReservations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success(reservationService.getPendingReservations());
    }

    @GetMapping("/all")
    public ApiResponse<List<Reservation>> getAllReservations(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success(reservationService.getAllReservations());
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<List<Reservation>> getRoomReservations(@PathVariable Long roomId) {
        return ApiResponse.success(reservationService.getRoomReservations(roomId));
    }

    @GetMapping("/room/{roomId}/date")
    public ApiResponse<List<Reservation>> getRoomReservationsByDate(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ApiResponse.success(reservationService.getRoomReservationsByDate(roomId, date));
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<Reservation> approveReservation(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success("审批通过", reservationService.approveReservation(id, userId));
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<Reservation> rejectReservation(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success("已拒绝", reservationService.rejectReservation(id, userId));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Reservation> cancelReservation(@PathVariable Long id,
                                                       @RequestBody Map<String, String> body,
                                                       HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        String cancelReason = body.getOrDefault("cancelReason", "用户主动取消");
        return ApiResponse.success("已取消", reservationService.cancelReservation(id, userId, cancelReason));
    }
}
