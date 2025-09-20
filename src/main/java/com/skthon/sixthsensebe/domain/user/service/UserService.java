package com.skthon.sixthsensebe.domain.user.service;

import com.skthon.sixthsensebe.domain.user.dto.request.UserDetailUpdateRequest;
import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.Gender;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.Personality;
import com.skthon.sixthsensebe.domain.user.entity.Role;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.mapper.UserMapper;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    final String username = normalizeUsername(request.getUsername());
    final String encodedPassword = passwordEncoder.encode(request.getPassword());
    final Role role = request.getRole() != null ? request.getRole() : Role.WORKER;

    if (userRepository.existsByUsername(username)) {
      throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
    }

    User user = userMapper.toEntity(request, encodedPassword)
        .toBuilder()
        .username(username)
        .role(role)
        .build();

    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
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

  /* 마이페이지 전체 수정 (PUT, JSON Body) */
  @Transactional
  public UserResponse replaceUserDetail(Long userId, UserDetailUpdateRequest req) {
    return applyDetailUpdate(
        userId,
        req.getName(),
        req.getBirthDate(),
        req.getGender(),
        req.getPhone(),
        req.getPersonality(),
        req.getHealth()
    );
  }

  /* 마이페이지 전체 수정 (PUT, RequestParam) */
  @Transactional
  public UserResponse replaceUserDetailParams(
      Long userId,
      String name,
      LocalDate birthDate,
      Gender gender,
      String phone,
      List<Personality> personality,
      List<Health> health
  ) {
    return applyDetailUpdate(userId, name, birthDate, gender, phone, personality, health);
  }

  // 공통 적용 로직
  private UserResponse applyDetailUpdate(
      Long userId,
      String name,
      LocalDate birthDate,
      Gender gender,
      String phone,
      List<Personality> personality,
      List<Health> health
  ) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    boolean isFirstTime =
        user.getBirthDate() == null &&
            user.getGender() == null &&
            user.getPhone() == null &&
            (user.getPersonalityList() == null || user.getPersonalityList().isEmpty()) &&
            (user.getHealthList() == null || user.getHealthList().isEmpty());

    user.setName(name);
    user.setBirthDate(birthDate);
    user.setGender(gender);
    user.setPhone(phone);

// personality
    if (user.getPersonalityList() == null) {
      user.setPersonalityList(new java.util.ArrayList<>());
    } else {
      user.getPersonalityList().clear();
    }
    if (personality != null) {
      user.getPersonalityList().addAll(
          personality.stream().filter(Objects::nonNull).toList() // 입력은 읽기전용이어도 addAll이 복사함
      );
    }

// health
    if (user.getHealthList() == null) {
      user.setHealthList(new java.util.ArrayList<>());
    } else {
      user.getHealthList().clear();
    }
    if (health != null) {
      user.getHealthList().addAll(
          health.stream().filter(Objects::nonNull).toList()
      );
    }

    userRepository.save(user);
    return userMapper.toResponse(user, isFirstTime);
  }

  // UserService 안에 추가
  @Transactional(readOnly = true)
  public UserResponse getUserDetail(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    boolean isFirstTime =
        user.getBirthDate() == null &&
            user.getGender() == null &&
            user.getPhone() == null &&
            (user.getPersonalityList() == null || user.getPersonalityList().isEmpty()) &&
            (user.getHealthList() == null || user.getHealthList().isEmpty());

    return userMapper.toResponse(user, isFirstTime);
  }

  private String normalizeUsername(String username) {
    return username == null ? null : username.trim().toLowerCase();
  }
}