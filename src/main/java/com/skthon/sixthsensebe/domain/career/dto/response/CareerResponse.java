package com.skthon.sixthsensebe.domain.career.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "Career 응답 DTO", description = "경력 정보를 반환하는 DTO")
public class CareerResponse {

  @Schema(description = "경력 ID", example = "1")
  private Long id;

  @Schema(description = "회사명", example = "무한상사")
  private String companyName;

  @Schema(description = "시작일", example = "2018-01-01")
  private LocalDate startDate;

  @Schema(description = "종료일(null이면 재직중)", example = "2020-12-31")
  private LocalDate endDate;

  @Schema(description = "재직중 여부", example = "true")
  private boolean current;

  @Schema(description = "생성 시각", example = "2025-09-20T12:34:56")
  private LocalDateTime createdAt;

  @Schema(description = "담당 업무", example = "택배 배달")
  private String task;
}
