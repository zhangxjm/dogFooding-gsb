package com.company.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.meeting.dto.MeetingRoomDTO;
import com.company.meeting.entity.MeetingRoom;
import com.company.meeting.vo.Result;

import java.util.List;

public interface MeetingRoomService extends IService<MeetingRoom> {
    
    Result<Void> addRoom(MeetingRoomDTO dto);
    
    Result<Void> updateRoom(MeetingRoomDTO dto);
    
    Result<Void> deleteRoom(Long id);
    
    Result<MeetingRoom> getRoomById(Long id);
    
    Result<List<MeetingRoom>> listAllRooms();
}
