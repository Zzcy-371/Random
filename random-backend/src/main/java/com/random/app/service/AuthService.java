package com.random.app.service;

import com.random.app.dto.LoginDTO;
import com.random.app.dto.RegisterDTO;
import com.random.app.vo.UserVO;

import java.util.Map;

public interface AuthService {
    Map<String, Object> register(RegisterDTO dto);
    Map<String, Object> login(LoginDTO dto);
    UserVO getCurrentUser(Long userId);
    UserVO updateCurrentUser(Long userId, RegisterDTO dto);
}
