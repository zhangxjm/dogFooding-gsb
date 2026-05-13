package com.company.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.meeting.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);
}
