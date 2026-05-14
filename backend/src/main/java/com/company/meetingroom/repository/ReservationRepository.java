package com.company.meetingroom.repository;

import com.company.meetingroom.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    List<Reservation> findByRoomIdOrderByStartTimeDesc(Long roomId);

    List<Reservation> findByStatusOrderByStartTimeDesc(String status);

    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
           "AND r.status IN ('PENDING', 'APPROVED') " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findConflicts(@Param("roomId") Long roomId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
           "AND r.status IN ('PENDING', 'APPROVED') " +
           "AND r.id <> :excludeId " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findConflictsExcludeSelf(@Param("roomId") Long roomId,
                                               @Param("excludeId") Long excludeId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId " +
           "AND r.status IN ('PENDING', 'APPROVED') " +
           "AND r.startTime >= :startOfDay AND r.startTime < :endOfDay " +
           "ORDER BY r.startTime")
    List<Reservation> findByRoomAndDate(@Param("roomId") Long roomId,
                                        @Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("endOfDay") LocalDateTime endOfDay);
}
