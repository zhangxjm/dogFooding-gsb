package com.company.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.meeting.dto.ApprovalDTO;
import com.company.meeting.dto.ReservationDTO;
import com.company.meeting.entity.Reservation;
import com.company.meeting.vo.Result;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService extends IService<Reservation> {
    
    Result<Void> createReservation(ReservationDTO dto);
    
    Result<Void> cancelReservation(Long id);
    
    Result<Void> approveReservation(ApprovalDTO dto);
    
    Result<List<Reservation>> getMyReservations();
    
    Result<List<Reservation>> getAllReservations();
    
    Result<Reservation> getReservationById(Long id);
    
    Result<List<Map<String, Object>>> getRoomTimeSlots(Long roomId, LocalDate date);
    
    Result<Boolean> checkConflict(Long roomId, String startTime, String endTime);
}
