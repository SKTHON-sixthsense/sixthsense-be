package com.skthon.sixthsensebe.domain.user.controller;

import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.service.UserService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API")
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<UserResponse>> register(
      @Valid @RequestBody UserRequest.SignupRequest request) {
    UserResponse response = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("회원 가입이 완료되었습니다.", response));
  }
}
