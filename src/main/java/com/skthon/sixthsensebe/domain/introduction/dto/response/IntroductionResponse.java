package com.skthon.sixthsensebe.domain.introduction.dto.response;

import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "IntroductionResponse DTO", description = "자기소개서 응답 DTO")
public class IntroductionResponse {

  @Schema(description = "자기소개서 ID", example = "1")
  private Long id;

  @Schema(description = "S3 이미지 URL", example = "https://s3.amazonaws.com/bucket/image.jpg")
  private String s3Url;

  @Schema(description = "이미지 파일명", example = "introduction.jpg")
  private String imageName;

  @Schema(description = "자기소개서 텍스트 내용")
  private String description;

  @Schema(description = "이미지 형식", example = "JPG")
  private ImageFormat imageFormat;

  @Schema(description = "OCR 처리 결과", example = "SUCCESS")
  private String inferResult;

  @Schema(description = "사용자 ID", example = "1")
  private Long userId;
}