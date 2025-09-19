package com.skthon.sixthsensebe.domain.user.dto.request;

import com.skthon.sixthsensebe.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

  @Getter
  @NoArgsConstructor
  public static class SignupRequest {

    @Schema(description = "아이디", example = "dasi")
    @NotBlank
    private String username;

    @Schema(description = "비밀번호", example = "dasi1234!")
    @NotBlank
    private String password;

    @Schema(description = "이름", example = "박명수")
    @NotBlank
    private String name;

    @NotNull(message = "role은 필수 값입니다.")
    @Schema(allowableValues = {"OWNER", "WORKER"}, example = "WORKER")
    private Role role;
  }

  @Getter
  @NoArgsConstructor
  public static class LoginRequest {

    @Schema(description = "아이디", example = "dasi")
    @NotBlank
    private String username;

    @Schema(description = "비밀번호", example = "dasi1234!")
    @NotBlank
    private String password;
  }
}
