package com.skthon.sixthsensebe.global.naverocr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OcrRequest {
  @Schema(description = "요청 버전", example = "V2")
  @JsonProperty("version")
  private String version; // V2

  @Schema(description = "ocr API로 보낼 요청 고유 id", example = "...")
  @JsonProperty("requestId")
  private String requestId; // 고유한 요청 ID (UUID)

  @Schema(description = "ocr API로 보낸 요청 시간", example = "...")
  @JsonProperty("timestamp")
  private Long timestamp; // 요청 시간

  @Schema(description = "파일 언어", example = "ko")
  @JsonProperty("lang")
  @Builder.Default
  private String lang = "ko"; // 한국어로 고정

  @Schema(description = "ocr API로 보낼 이미지 배열", example = "...")
  @JsonProperty("images")
  private List<ImageRequest> images; // 처리할 이미지 목록(1개)

  @JsonProperty("enableTableDetection")
  @Builder.Default
  private Boolean enableTableDetection = false; // 테이블 감지 여부 (표 인식 여부 -> 손글씨 이미지이기 때문에 false로 설정)
}
