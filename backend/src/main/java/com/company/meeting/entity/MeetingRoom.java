package com.company.meeting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("meeting_rooms")
public class MeetingRoom {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String location;
    
    private Integer capacity;
    
    private String facilities;
    
    private String description;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
