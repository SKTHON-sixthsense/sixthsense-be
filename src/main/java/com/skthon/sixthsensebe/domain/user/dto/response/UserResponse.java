package com.skthon.sixthsensebe.domain.user.dto.response;

import com.skthon.sixthsensebe.domain.user.entity.Gender;
import com.skthon.sixthsensebe.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
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

  @Schema(description = "사용자 역할", example = "WORKER", allowableValues = {"OWNER", "WORKER"})
  private Role role;

  @Schema(description = "생년월일", example = "1970-05-12")
  private LocalDate birthDate;

  @Schema(description = "나이", example = "55")
  private Integer age;

  @Schema(description = "성별", example = "MALE")
  private Gender gender;

  @Schema(description = "전화번호", example = "010-1234-5678")
  private String phone;

  @ArraySchema(schema = @Schema(implementation = TagDto.class, description = "성격 태그 (code+한글명)"))
  private List<TagDto> personality;

  @ArraySchema(schema = @Schema(implementation = TagDto.class, description = "건강 태그 (code+한글명)"))
  private List<TagDto> health;

  @Schema(description = "마이페이지 최초 입력 여부", example = "true")
  private Boolean firstTime;
}
