package com.company.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.meeting.dto.LoginDTO;
import com.company.meeting.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    
    Map<String, Object> login(LoginDTO loginDTO);
    
    User getCurrentUser();
    
    Long getCurrentUserId();
}
