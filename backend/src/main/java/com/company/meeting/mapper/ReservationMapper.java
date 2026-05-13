package com.company.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.meeting.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    @Select("SELECT r.*, mr.name as roomName, u.real_name as userName " +
            "FROM reservations r " +
            "LEFT JOIN meeting_rooms mr ON r.room_id = mr.id " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.id = #{id}")
    Reservation selectByIdWithDetails(Long id);
    
    @Select("SELECT r.*, mr.name as roomName, u.real_name as userName " +
            "FROM reservations r " +
            "LEFT JOIN meeting_rooms mr ON r.room_id = mr.id " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_at DESC")
    List<Reservation> selectByUserId(Long userId);
    
    @Select("SELECT r.*, mr.name as roomName, u.real_name as userName " +
            "FROM reservations r " +
            "LEFT JOIN meeting_rooms mr ON r.room_id = mr.id " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "ORDER BY r.created_at DESC")
    List<Reservation> selectAllWithDetails();
    
    @Select("SELECT COUNT(*) FROM reservations " +
            "WHERE room_id = #{roomId} " +
            "AND status IN ('PENDING', 'APPROVED') " +
            "AND ((start_time <= #{startTime} AND end_time > #{startTime}) " +
            "OR (start_time < #{endTime} AND end_time >= #{endTime}) " +
            "OR (start_time >= #{startTime} AND end_time <= #{endTime}))")
    int countConflicts(@Param("roomId") Long roomId, 
                       @Param("startTime") LocalDateTime startTime, 
                       @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT r.*, mr.name as roomName, u.real_name as userName " +
            "FROM reservations r " +
            "LEFT JOIN meeting_rooms mr ON r.room_id = mr.id " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.room_id = #{roomId} " +
            "AND r.status IN ('PENDING', 'APPROVED') " +
            "AND DATE(r.start_time) = #{date} " +
            "ORDER BY r.start_time")
    List<Reservation> selectByRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") String date);
}
