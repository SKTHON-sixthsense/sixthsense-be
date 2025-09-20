package com.skthon.sixthsensebe.domain.user.controller;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.Gender;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.Personality;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.service.UserService;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.jwt.JwtProvider;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API")
public class UserController {

  private final UserService userService;
  private final JwtProvider jwtProvider;

  private Long currentUserId(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return jwtProvider.extractUserId(bearer.substring(7));
    }
    throw new CustomException(UserErrorCode.USER_NOT_FOUND);
  }

  // 회원가입
  @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
  @PostMapping(
      value = "/register",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<UserResponse>> register(
      @Valid @RequestBody UserRequest.SignupRequest request) {
    UserResponse response = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("회원 가입이 완료되었습니다.", response));
  }

  // 마이페이지 조회
  @Operation(summary = "내 마이페이지 조회")
  @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<UserResponse>> getMyDetail(HttpServletRequest request) {
    UserResponse data = userService.getUserDetail(currentUserId(request));
    return ResponseEntity.ok(BaseResponse.success("내 정보 조회에 성공했습니다.", data));
  }

  @PutMapping("/detail")
  @Operation(
      summary = "내 마이페이지 수정",
      description = "기본 정보는 문자열 입력, 성격/건강은 Enum 선택으로 받습니다."
  )
  public ResponseEntity<BaseResponse<UserResponse>> putMyDetail(
      HttpServletRequest request,

      @Parameter(description = "이름", example = "박명수")
      @RequestParam(required = false) String name,

      @Parameter(description = "생년월일", example = "1970-05-12")
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,

      @Parameter(description = "휴대폰 번호", example = "010-1234-5678")
      @RequestParam(required = false) String phone,

      @Parameter(description = "성별", schema = @Schema(implementation = Gender.class), example = "MALE")
      @RequestParam(required = false) Gender gender,

      @Parameter(description = "성격 태그(멀티)", array = @ArraySchema(schema = @Schema(implementation = Personality.class)))
      @RequestParam(required = false) List<Personality> personality,

      @Parameter(description = "건강 태그(멀티)", array = @ArraySchema(schema = @Schema(implementation = Health.class)))
      @RequestParam(required = false) List<Health> health
  ) {
    UserResponse data = userService.replaceUserDetailParams(
        currentUserId(request),
        name,
        birthDate,
        gender,
        phone,
        personality,
        health
    );

    String message = Boolean.TRUE.equals(data.getFirstTime())
        ? "내 정보 최초 등록 성공"
        : "내 정보 수정 성공";

    return ResponseEntity.ok(BaseResponse.success(message, data));
  }

  // 특정 유저 상세 조회
  @Operation(summary = "특정 유저 상세 조회", description = "사장님만 접근 가능합니다.")
  @GetMapping("/{id}/detail")
  @PreAuthorize("hasRole('OWNER')")
  public ResponseEntity<BaseResponse<UserResponse>> getUserDetail(@PathVariable Long id) {
    var data = userService.getUserDetail(id);
    return ResponseEntity.ok(BaseResponse.success("유저 정보 조회에 성공했습니다.", data));
  }
}