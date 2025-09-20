package com.skthon.sixthsensebe.domain.user.dto.request;

import com.skthon.sixthsensebe.domain.user.entity.Gender;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.Personality;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "UserDetailUpdateRequest", description = "마이페이지 수정 요청 DTO")
public class UserDetailUpdateRequest {

  @Schema(description = "이름", example = "박명수")
  @Size(max = 20)
  private String name;

  @Schema(description = "생년월일 (yyyy-MM-dd)", example = "1970-05-12")
  private LocalDate birthDate;

  @Schema(description = "성별", example = "MALE")
  private Gender gender;

  @Schema(description = "휴대폰 번호", example = "010-1234-5678")
  @Size(max = 20)
  private String phone;

  @ArraySchema(schema = @Schema(implementation = Personality.class))
  private List<Personality> personality;

  @ArraySchema(schema = @Schema(implementation = Health.class))
  private List<Health> health;
}
