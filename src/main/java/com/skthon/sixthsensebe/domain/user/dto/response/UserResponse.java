package com.skthon.sixthsensebe.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserResponse DTO", description = "사용자 관련 응답 DTO")
public class UserResponse {

  @Schema(description = "사용자 ID", example = "1")
  private Long id;

  @Schema(description = "아이디", example = "dasi")
  private String username;

  @Schema(description = "이름", example = "박명수")
  private String name;
}
