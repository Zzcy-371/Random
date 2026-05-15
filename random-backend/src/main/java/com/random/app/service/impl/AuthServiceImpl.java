package com.random.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.random.app.dto.LoginDTO;
import com.random.app.dto.RegisterDTO;
import com.random.app.dto.WxLoginDTO;
import com.random.app.entity.User;
import com.random.app.exception.BusinessException;
import com.random.app.repository.UserRepository;
import com.random.app.service.AuthService;
import com.random.app.util.JwtUtil;
import com.random.app.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${wx.appid:}")
    private String wxAppId;

    @Value("${wx.secret:}")
    private String wxSecret;

    @Override
    public Map<String, Object> register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toVO(user));
        return result;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toVO(user));
        return result;
    }

    @Override
    public Map<String, Object> wxLogin(WxLoginDTO dto) {
        if (wxAppId.isBlank() || wxSecret.isBlank()) {
            throw new BusinessException("微信登录未配置");
        }

        // 调用微信 jscode2session 接口获取 openid
        String openid;
        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wxAppId, wxSecret, dto.getCode());
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = objectMapper.readTree(response.body());

            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
                String errMsg = json.has("errmsg") ? json.get("errmsg").asText() : "未知错误";
                log.error("微信登录失败: {}", errMsg);
                throw new BusinessException("微信登录失败: " + errMsg);
            }
            openid = json.get("openid").asText();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常: {}", e.getMessage());
            throw new BusinessException("微信登录服务异常，请稍后重试");
        }

        // 查找或创建用户
        User user = userRepository.findByWxOpenid(openid).orElseGet(() -> {
            User newUser = new User();
            newUser.setWxOpenid(openid);
            newUser.setUsername("wx_" + openid.substring(0, 8));
            newUser.setNickname("微信用户");
            newUser.setPasswordHash(null); // 微信用户无密码
            return userRepository.save(newUser);
        });

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toVO(user));
        return result;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toVO(user);
    }

    @Override
    public UserVO updateCurrentUser(Long userId, RegisterDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        userRepository.save(user);
        return toVO(user);
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
