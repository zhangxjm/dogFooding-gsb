package com.company.meetingroom.service;

import com.company.meetingroom.entity.MeetingRoom;
import com.company.meetingroom.exception.BusinessException;
import com.company.meetingroom.repository.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoom> getAllRooms() {
        return meetingRoomRepository.findAll();
    }

    public List<MeetingRoom> getAvailableRooms() {
        return meetingRoomRepository.findAll().stream()
                .filter(room -> room.getStatus() == 1)
                .toList();
    }

    public MeetingRoom getRoomById(Long id) {
        return meetingRoomRepository.findById(id)
                .orElseThrow(() -> new BusinessException("会议室不存在"));
    }

    public MeetingRoom createRoom(MeetingRoom room) {
        return meetingRoomRepository.save(room);
    }

    public MeetingRoom updateRoom(Long id, MeetingRoom roomDetails) {
        MeetingRoom room = getRoomById(id);
        room.setName(roomDetails.getName());
        room.setLocation(roomDetails.getLocation());
        room.setCapacity(roomDetails.getCapacity());
        room.setEquipment(roomDetails.getEquipment());
        room.setStatus(roomDetails.getStatus());
        room.setDescription(roomDetails.getDescription());
        return meetingRoomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        if (!meetingRoomRepository.existsById(id)) {
            throw new BusinessException("会议室不存在");
        }
        meetingRoomRepository.deleteById(id);
    }
}
