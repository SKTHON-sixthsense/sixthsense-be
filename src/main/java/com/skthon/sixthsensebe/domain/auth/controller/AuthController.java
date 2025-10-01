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
      description = """
            아이디/비밀번호로 로그인합니다.
            - 성공 시: accessToken/refreshToken 모두 **HttpOnly 쿠키**로 전달됩니다.
            - 응답 바디에는 로그인한 사용자 정보가 포함됩니다.
          """
  )
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
      description = """
            쿠키의 refreshToken을 검증하여 **새로운 accessToken을 HttpOnly 쿠키**로 재발급합니다.
            - Redis 저장 토큰과 일치할 때만 성공합니다.
          """
  )
  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<String>> reissueAccessToken(
      HttpServletRequest request, HttpServletResponse response) {

    authService.reissueAccessToken(request, response);
    return ResponseEntity.ok(BaseResponse.success("AccessToken 재발급 성공"));
  }

  @Operation(
      summary = "로그아웃",
      description = """
            쿠키의 accessToken을 블랙리스트에 등록하고, Redis의 refreshToken을 삭제합니다.
            - 브라우저의 accessToken/refreshToken 쿠키를 즉시 만료시킵니다.
          """
  )
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      HttpServletRequest request, HttpServletResponse response) {
    authService.logout(request, response);
    return ResponseEntity.ok(BaseResponse.success("로그아웃 성공", null));
  }
}
