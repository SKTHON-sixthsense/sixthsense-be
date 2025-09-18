package com.skthon.sixthsensebe.domain.user.service;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.mapper.UserMapper;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;            // toEntity(), toResponse() 제공
  private final PasswordEncoder passwordEncoder;  // BCryptPasswordEncoder 등

  /* 회원가입 */
  @Transactional
  public UserResponse register(UserRequest.SignupRequest request) {
    final String username = request.getUsername();
    if (userRepository.existsByUsername(username)) {
      throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
    }

    final String encodedPassword = passwordEncoder.encode(request.getPassword());
    User user = userMapper.toEntity(request, encodedPassword);
    user = userRepository.save(user);

    // 토큰 발급이 없다면 일반 응답
    return userMapper.toResponse(user);
  }

  /* 로그인 */
  @Transactional(readOnly = true)
  public UserResponse login(UserRequest.LoginRequest request) {
    final String username = request.getUsername();
    final String rawPassword = request.getPassword();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new CustomException(UserErrorCode.INVALID_PASSWORD);
    }

    // JWT 발급이 있으면 여기서 accessToken 생성 후 전달
    String accessToken = null; // ex) jwtProvider.createToken(user.getId(), user.getUsername());
    return userMapper.toLoginResponse(user, accessToken);
  }
}
