package com.random.app.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
