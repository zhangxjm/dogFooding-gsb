package com.company.meeting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reservations")
public class Reservation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roomId;
    
    private Long userId;
    
    private String title;
    
    private Integer attendees;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private String description;
    
    private String status;
    
    private Long approverId;
    
    private LocalDateTime approvalTime;
    
    private String approvalRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private String roomName;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String approverName;
}
