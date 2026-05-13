package com.company.meeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    
    private Long id;
    
    @NotNull(message = "会议室不能为空")
    private Long roomId;
    
    @NotBlank(message = "会议主题不能为空")
    private String title;
    
    @NotNull(message = "参会人数不能为空")
    private Integer attendees;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    private String description;
}
