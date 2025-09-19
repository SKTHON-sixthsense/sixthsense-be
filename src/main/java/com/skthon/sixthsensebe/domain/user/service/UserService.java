package com.skthon.sixthsensebe.domain.user.service;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.Role;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.mapper.UserMapper;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper; // toEntity(), toResponse() 제공
  private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder 등

  /* 회원가입 */
  @Transactional
  public UserResponse register(UserRequest.SignupRequest request) {
    // 입력 정규화
    final String username = normalizeUsername(request.getUsername());
    final String encodedPassword = passwordEncoder.encode(request.getPassword());
    final Role role = request.getRole() != null ? request.getRole() : Role.WORKER;

    if (userRepository.existsByUsername(username)) {
      throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
    }

    // 엔티티 생성 (mapper에서 기본값 보정해도 좋음)
    User user = userMapper.toEntity(request, encodedPassword)
        .toBuilder()
        .username(username)
        .role(role)
        .build();

    // 저장 (DB UNIQUE 제약 위반 시 예외로 변환)
    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      // username UNIQUE 위반 등
      throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
    } catch (Exception e) {
      throw new CustomException(UserErrorCode.USER_SAVE_FAILED);
    }

    return userMapper.toResponse(user);
  }

  /* 로그인 */
  public UserResponse login(UserRequest.LoginRequest request) {
    final String username = normalizeUsername(request.getUsername());
    final String rawPassword = request.getPassword();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new CustomException(UserErrorCode.INVALID_PASSWORD);
    }

    String accessToken = null; // 필요 시 주입/위임
    return userMapper.toLoginResponse(user, accessToken);
  }

  private String normalizeUsername(String username) {
    if (username == null) {
      return null;
    }
    return username.trim().toLowerCase();
  }
}