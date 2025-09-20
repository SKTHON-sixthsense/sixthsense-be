package com.skthon.sixthsensebe.domain.career.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CareerRequest {

  @Getter
  @NoArgsConstructor
  public static class Create {

    @Schema(description = "회사명", example = "무한상사")
    @NotBlank
    private String companyName;

    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;      // 재직 중이면 null

    @Schema(description = "담당 업무", example = "택배 배달")
    private String task;

  }
}
