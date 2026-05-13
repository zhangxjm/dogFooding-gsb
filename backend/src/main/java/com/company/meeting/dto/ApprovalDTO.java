package com.company.meeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalDTO {
    
    @NotNull(message = "预约ID不能为空")
    private Long reservationId;
    
    @NotBlank(message = "审批状态不能为空")
    private String status;
    
    private String remark;
}
