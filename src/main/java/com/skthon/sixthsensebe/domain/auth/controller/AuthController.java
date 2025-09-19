package com.skthon.sixthsensebe.domain.auth.controller;

import com.skthon.sixthsensebe.domain.auth.service.AuthService;
import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth 관련 API")
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "로그인",
      description =
          """
              아이디와 비밀번호로 로그인을 수행합니다.
              
              - 성공 시: 액세스 토큰은 헤더(**Authorization**)로, 리프레시 토큰은 **HttpOnly 쿠키**로 전달됩니다.
              - 응답 바디에는 로그인한 사용자의 기본 정보가 포함됩니다.
              """)
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<UserResponse>> login(
      @Valid @RequestBody UserRequest.LoginRequest loginRequest, HttpServletResponse response) {
    UserResponse userResponse = authService.login(loginRequest, response);
    return ResponseEntity.ok(BaseResponse.success("로그인 성공", userResponse));
  }

  @Operation(
      summary = "Worker 테스트 로그인",
      description = """
            테스트용 구직자(Worker) 계정으로 즉시 로그인합니다.
            - 토큰 발급 및 응답은 일반 로그인과 동일합니다.
          """)
  @PostMapping("/login/test/worker")
  public ResponseEntity<BaseResponse<UserResponse>> testLoginWorker(HttpServletResponse response) {
    UserResponse userResponse = authService.testLoginWorker(response);
    return ResponseEntity.ok(BaseResponse.success("테스트 로그인(Worker) 성공", userResponse));
  }

  @Operation(
      summary = "Owner 테스트 로그인",
      description = """
            테스트용 사장님(Owner) 계정으로 즉시 로그인합니다.
            - 토큰 발급 및 응답은 일반 로그인과 동일합니다.
          """)
  @PostMapping("/login/test/owner")
  public ResponseEntity<BaseResponse<UserResponse>> testLoginOwner(HttpServletResponse response) {
    UserResponse userResponse = authService.testLoginOwner(response);
    return ResponseEntity.ok(BaseResponse.success("테스트 로그인(Owner) 성공", userResponse));
  }

  @Operation(
      summary = "액세스 토큰 재발급",
      description =
          """
              저장된 리프레시 토큰을 기반으로 새로운 액세스 토큰을 재발급합니다.
              - 요청 시 **쿠키에 저장된 refreshToken**을 사용합니다.
              - Redis에 저장된 토큰과 일치하는 경우에만 재발급이 성공합니다.
              - 응답 헤더의 Authorization 값으로 새로운 액세스 토큰이 전달됩니다.
              - 본 API는 **로그인 후 액세스 토큰이 만료된 경우에 사용**합니다.
              """)
  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<String>> reissueAccessToken(
      HttpServletRequest request, HttpServletResponse response) {

    authService.reissueAccessToken(request, response);
    return ResponseEntity.ok(BaseResponse.success("AccessToken 재발급 성공"));
  }

  @Operation(
      summary = "로그아웃",
      description =
          """
              현재 로그인된 사용자를 로그아웃 처리합니다.
              
              - 요청 헤더의 **Authorization**에서 액세스 토큰을 추출하여 로그아웃 처리합니다.
              - 액세스 토큰은 Redis 블랙리스트에 등록되어 만료 전까지 사용이 차단됩니다.
              - 리프레시 토큰은 Redis에서 삭제되며, **브라우저 쿠키에서도 제거**됩니다.
              - 응답 바디는 별도의 데이터 없이 로그아웃 성공 메시지만 반환됩니다.
              """)
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      HttpServletRequest request, HttpServletResponse response) {
    authService.logout(request, response);
    return ResponseEntity.ok(BaseResponse.success("로그아웃 성공", null));
  }
}
