package com.company.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.meeting.dto.MeetingRoomDTO;
import com.company.meeting.entity.MeetingRoom;
import com.company.meeting.exception.BusinessException;
import com.company.meeting.mapper.MeetingRoomMapper;
import com.company.meeting.service.MeetingRoomService;
import com.company.meeting.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements MeetingRoomService {
    
    @Override
    public Result<Void> addRoom(MeetingRoomDTO dto) {
        MeetingRoom room = new MeetingRoom();
        BeanUtils.copyProperties(dto, room);
        room.setStatus(1);
        save(room);
        return Result.success();
    }
    
    @Override
    public Result<Void> updateRoom(MeetingRoomDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("会议室ID不能为空");
        }
        MeetingRoom room = getById(dto.getId());
        if (room == null) {
            throw new BusinessException("会议室不存在");
        }
        BeanUtils.copyProperties(dto, room);
        updateById(room);
        return Result.success();
    }
    
    @Override
    public Result<Void> deleteRoom(Long id) {
        MeetingRoom room = getById(id);
        if (room == null) {
            throw new BusinessException("会议室不存在");
        }
        removeById(id);
        return Result.success();
    }
    
    @Override
    public Result<MeetingRoom> getRoomById(Long id) {
        MeetingRoom room = getById(id);
        if (room == null) {
            throw new BusinessException("会议室不存在");
        }
        return Result.success(room);
    }
    
    @Override
    public Result<List<MeetingRoom>> listAllRooms() {
        List<MeetingRoom> list = lambdaQuery()
                .eq(MeetingRoom::getStatus, 1)
                .orderByAsc(MeetingRoom::getId)
                .list();
        return Result.success(list);
    }
}
