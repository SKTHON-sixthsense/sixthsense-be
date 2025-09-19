package com.skthon.sixthsensebe.domain.user.mapper;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.Role;
import com.skthon.sixthsensebe.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toEntity(UserRequest.SignupRequest req, String encodedPassword) {
    return User.builder()
        .username(req.getUsername() != null ? req.getUsername().trim().toLowerCase() : null)
        .password(encodedPassword)
        .name(req.getName())
        // role이 null이면 기본값 WORKER로 보정
        .role(req.getRole() != null ? req.getRole() : Role.WORKER)
        .build();
  }

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .role(user.getRole())
        .build();
  }

  public UserResponse toLoginResponse(User user, String accessToken) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .role(user.getRole())
        // 필요하면 accessToken도 UserResponse에 포함 가능
        .build();
  }
}
