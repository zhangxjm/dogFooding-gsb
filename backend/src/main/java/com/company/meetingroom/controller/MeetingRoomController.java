package com.company.meetingroom.controller;

import com.company.meetingroom.dto.ApiResponse;
import com.company.meetingroom.entity.MeetingRoom;
import com.company.meetingroom.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @GetMapping("/list")
    public ApiResponse<List<MeetingRoom>> getAllRooms() {
        return ApiResponse.success(meetingRoomService.getAllRooms());
    }

    @GetMapping("/available")
    public ApiResponse<List<MeetingRoom>> getAvailableRooms() {
        return ApiResponse.success(meetingRoomService.getAvailableRooms());
    }

    @GetMapping("/{id}")
    public ApiResponse<MeetingRoom> getRoomById(@PathVariable Long id) {
        return ApiResponse.success(meetingRoomService.getRoomById(id));
    }

    @PostMapping
    public ApiResponse<MeetingRoom> createRoom(@RequestBody MeetingRoom room) {
        return ApiResponse.success("创建成功", meetingRoomService.createRoom(room));
    }

    @PutMapping("/{id}")
    public ApiResponse<MeetingRoom> updateRoom(@PathVariable Long id, @RequestBody MeetingRoom room) {
        return ApiResponse.success("更新成功", meetingRoomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable Long id) {
        meetingRoomService.deleteRoom(id);
        return ApiResponse.success("删除成功", null);
    }
}
