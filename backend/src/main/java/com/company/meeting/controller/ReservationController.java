package com.company.meeting.controller;

import com.company.meeting.dto.ApprovalDTO;
import com.company.meeting.dto.ReservationDTO;
import com.company.meeting.entity.Reservation;
import com.company.meeting.service.ReservationService;
import com.company.meeting.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ReservationDTO dto) {
        return reservationService.createReservation(dto);
    }
    
    @GetMapping("/my")
    public Result<List<Reservation>> getMyReservations() {
        return reservationService.getMyReservations();
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Reservation>> getAllReservations() {
        return reservationService.getAllReservations();
    }
    
    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }
    
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }
    
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> approve(@Valid @RequestBody ApprovalDTO dto) {
        return reservationService.approveReservation(dto);
    }
    
    @GetMapping("/room/{roomId}/timeslots")
    public Result<List<Map<String, Object>>> getTimeSlots(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.getRoomTimeSlots(roomId, date);
    }
    
    @GetMapping("/check-conflict")
    public Result<Boolean> checkConflict(
            @RequestParam Long roomId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return reservationService.checkConflict(roomId, startTime, endTime);
    }
}
