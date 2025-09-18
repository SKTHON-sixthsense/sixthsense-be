package com.skthon.sixthsensebe.domain.user.mapper;

import org.springframework.stereotype.Component;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.User;

@Component
public class UserMapper {

  public User toEntity(UserRequest.SignupRequest req, String encodedPassword) {
    return User.builder()
        .username(req.getUsername())
        .password(encodedPassword)
        .name(req.getName())
        .build();
  }

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .build();
  }

  public UserResponse toLoginResponse(User user, String accessToken) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .build();
  }
}
