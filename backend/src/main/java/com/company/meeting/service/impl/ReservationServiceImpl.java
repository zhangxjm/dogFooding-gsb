package com.company.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.meeting.dto.ApprovalDTO;
import com.company.meeting.dto.ReservationDTO;
import com.company.meeting.entity.MeetingRoom;
import com.company.meeting.entity.Reservation;
import com.company.meeting.entity.User;
import com.company.meeting.exception.BusinessException;
import com.company.meeting.mapper.ReservationMapper;
import com.company.meeting.service.MeetingRoomService;
import com.company.meeting.service.ReservationService;
import com.company.meeting.service.UserService;
import com.company.meeting.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MeetingRoomService meetingRoomService;
    
    @Override
    @Transactional
    public Result<Void> createReservation(ReservationDTO dto) {
        // 验证时间
        if (dto.getEndTime().isBefore(dto.getStartTime()) || 
            dto.getEndTime().equals(dto.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("不能预约过去的时间");
        }
        
        // 验证会议室
        MeetingRoom room = meetingRoomService.getById(dto.getRoomId());
        if (room == null) {
            throw new BusinessException("会议室不存在");
        }
        
        if (room.getStatus() != 1) {
            throw new BusinessException("会议室不可用");
        }
        
        // 验证人数
        if (dto.getAttendees() > room.getCapacity()) {
            throw new BusinessException("参会人数超过会议室容量");
        }
        
        // 检查冲突
        int conflicts = baseMapper.countConflicts(dto.getRoomId(), dto.getStartTime(), dto.getEndTime());
        if (conflicts > 0) {
            throw new BusinessException("该时间段已被预约");
        }
        
        // 创建预约
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(dto, reservation);
        reservation.setUserId(userService.getCurrentUserId());
        reservation.setStatus("PENDING");
        
        save(reservation);
        return Result.success();
    }
    
    @Override
    @Transactional
    public Result<Void> cancelReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        Long currentUserId = userService.getCurrentUserId();
        User currentUser = userService.getById(currentUserId);
        
        // 只有预约人或管理员可以取消
        if (!reservation.getUserId().equals(currentUserId) && !"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("无权取消此预约");
        }
        
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new BusinessException("预约已取消");
        }
        
        if ("COMPLETED".equals(reservation.getStatus())) {
            throw new BusinessException("会议已完成，无法取消");
        }
        
        reservation.setStatus("CANCELLED");
        updateById(reservation);
        return Result.success();
    }
    
    @Override
    @Transactional
    public Result<Void> approveReservation(ApprovalDTO dto) {
        Reservation reservation = getById(dto.getReservationId());
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new BusinessException("该预约已处理");
        }
        
        Long currentUserId = userService.getCurrentUserId();
        
        reservation.setStatus(dto.getStatus());
        reservation.setApproverId(currentUserId);
        reservation.setApprovalTime(LocalDateTime.now());
        reservation.setApprovalRemark(dto.getRemark());
        
        updateById(reservation);
        return Result.success();
    }
    
    @Override
    public Result<List<Reservation>> getMyReservations() {
        Long userId = userService.getCurrentUserId();
        List<Reservation> list = baseMapper.selectByUserId(userId);
        return Result.success(list);
    }
    
    @Override
    public Result<List<Reservation>> getAllReservations() {
        List<Reservation> list = baseMapper.selectAllWithDetails();
        return Result.success(list);
    }
    
    @Override
    public Result<Reservation> getReservationById(Long id) {
        Reservation reservation = baseMapper.selectByIdWithDetails(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        return Result.success(reservation);
    }
    
    @Override
    public Result<List<Map<String, Object>>> getRoomTimeSlots(Long roomId, LocalDate date) {
        String dateStr = date.toString();
        List<Reservation> reservations = baseMapper.selectByRoomIdAndDate(roomId, dateStr);
        
        // 生成时间段（8:00 - 18:00，每30分钟一个槽位）
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (int hour = 8; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                LocalDateTime slotStart = date.atTime(hour, minute);
                LocalDateTime slotEnd = slotStart.plusMinutes(30);
                
                Map<String, Object> slot = new HashMap<>();
                slot.put("startTime", slotStart.format(formatter));
                slot.put("endTime", slotEnd.format(formatter));
                slot.put("available", true);
                slot.put("reservation", null);
                
                // 检查该时间段是否被占用
                for (Reservation r : reservations) {
                    if ((r.getStartTime().isBefore(slotEnd) || r.getStartTime().isEqual(slotEnd)) &&
                        (r.getEndTime().isAfter(slotStart) || r.getEndTime().isEqual(slotStart))) {
                        slot.put("available", false);
                        slot.put("reservation", r);
                        break;
                    }
                }
                
                timeSlots.add(slot);
            }
        }
        
        return Result.success(timeSlots);
    }
    
    @Override
    public Result<Boolean> checkConflict(Long roomId, String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        
        int conflicts = baseMapper.countConflicts(roomId, start, end);
        return Result.success(conflicts > 0);
    }
}
