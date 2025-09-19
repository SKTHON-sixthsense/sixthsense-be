package com.skthon.sixthsensebe.global.naverocr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TextField {

  @Schema(description = "인식된 텍스트", example = "손글씨로 작성된 텍스트")
  @JsonProperty("inferText")
  private String inferText;

  @Schema(description = "인식 신뢰도", example = "0.95")
  @JsonProperty("inferConfidence")
  private Double inferConfidence;
}
