package com.random.app.controller;

import com.random.app.dto.LoginDTO;
import com.random.app.dto.RegisterDTO;
import com.random.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterDTO dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "注册成功");
        result.put("data", authService.register(dto));
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "登录成功");
        result.put("data", authService.login(dto));
        return result;
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", authService.getCurrentUser(userId));
        return result;
    }

    @PutMapping("/me")
    public Map<String, Object> updateCurrentUser(Authentication auth, @RequestBody RegisterDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "更新成功");
        result.put("data", authService.updateCurrentUser(userId, dto));
        return result;
    }
}
