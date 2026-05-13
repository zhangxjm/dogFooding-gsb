package com.company.meeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MeetingRoomDTO {
    
    private Long id;
    
    @NotBlank(message = "会议室名称不能为空")
    private String name;
    
    private String location;
    
    @NotNull(message = "容纳人数不能为空")
    private Integer capacity;
    
    private String facilities;
    
    private String description;
    
    private Integer status;
}
