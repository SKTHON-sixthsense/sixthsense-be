package com.skthon.sixthsensebe.global.naverocr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OcrResponse {

  @Schema(description = "이미지 인식 결과", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILURE", "ERROR"})
  @JsonProperty("inferResult")
  private String inferResult;

  @Schema(description = "인식된 텍스트 필드 목록")
  @JsonProperty("fields")
  private List<TextField> fields;

}
