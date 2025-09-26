package com.skthon.sixthsensebe.domain.user.mapper;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.TagDto;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.Personality;
import com.skthon.sixthsensebe.domain.user.entity.Role;
import com.skthon.sixthsensebe.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserMapper {

  public User toEntity(UserRequest.SignupRequest req, String encodedPassword) {
    return User.builder()
        .username(req.getUsername() != null ? req.getUsername().trim().toLowerCase() : null)
        .password(encodedPassword)
        .name(req.getName())
        .s3url(null) // 초기에는 프로필 사진 null
        // role이 null이면 기본값 WORKER로 보정
        .role(req.getRole() != null ? req.getRole() : Role.WORKER)
        .build();
  }

  public UserResponse toResponse(User user) {
    return toResponse(user, null); // firstTime 없음
  }

  public UserResponse toResponse(User user, Boolean firstTime) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .s3url(user.getS3url())
        .role(user.getRole())
        .birthDate(user.getBirthDate())
        .age(calcAge(user.getBirthDate()))
        .gender(user.getGender())
        .phone(user.getPhone())
        .personality(mapPersonality(user.getPersonalityList()))
        .health(mapHealth(user.getHealthList()))
        .firstTime(firstTime)
        .build();
  }

  public UserResponse toLoginResponse(User user, String accessToken) {
    return toResponse(user); // 동일 포맷 사용
  }

  private Integer calcAge(LocalDate birthDate) {
    if (birthDate == null) {
      return null;
    }
    return Period.between(birthDate, LocalDate.now()).getYears();
  }

  private List<TagDto> mapPersonality(List<Personality> list) {
    if (list == null) {
      return null;
    }
    return list.stream()
        .filter(Objects::nonNull)
        .map(p -> TagDto.of(p.getCode(), p.getKoName()))
        .collect(Collectors.toList());
  }

  private List<TagDto> mapHealth(List<Health> list) {
    if (list == null) {
      return null;
    }
    return list.stream()
        .filter(Objects::nonNull)
        .map(h -> TagDto.of(h.getCode(), h.getKoName()))
        .collect(Collectors.toList());
  }
}
