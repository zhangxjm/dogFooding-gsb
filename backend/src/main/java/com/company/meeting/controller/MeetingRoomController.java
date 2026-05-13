package com.company.meeting.controller;

import com.company.meeting.dto.MeetingRoomDTO;
import com.company.meeting.entity.MeetingRoom;
import com.company.meeting.service.MeetingRoomService;
import com.company.meeting.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class MeetingRoomController {
    
    @Autowired
    private MeetingRoomService meetingRoomService;
    
    @GetMapping
    public Result<List<MeetingRoom>> list() {
        return meetingRoomService.listAllRooms();
    }
    
    @GetMapping("/{id}")
    public Result<MeetingRoom> getById(@PathVariable Long id) {
        return meetingRoomService.getRoomById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@Valid @RequestBody MeetingRoomDTO dto) {
        return meetingRoomService.addRoom(dto);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MeetingRoomDTO dto) {
        dto.setId(id);
        return meetingRoomService.updateRoom(dto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return meetingRoomService.deleteRoom(id);
    }
}
