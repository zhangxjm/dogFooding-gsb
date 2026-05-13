package com.company.meeting.controller;

import com.company.meeting.dto.LoginDTO;
import com.company.meeting.service.UserService;
import com.company.meeting.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success(result);
    }
    
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo() {
        Map<String, Object> result = Map.of(
            "user", userService.getCurrentUser()
        );
        return Result.success(result);
    }
}
