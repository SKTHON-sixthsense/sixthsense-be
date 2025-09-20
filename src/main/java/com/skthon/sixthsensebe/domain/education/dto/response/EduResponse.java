package com.skthon.sixthsensebe.domain.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@Schema(title = "EduResponse DTO", description = "교육공고 응답")
public class EduResponse {

  @Schema(description = "교육공고 id", example = "1")
  private Long id;

  @Schema(description = "교육공고 제목", example = "간병사")
  private String title;

  @Schema(description = "교육공고 설명", example = "다양한 돌봄 현장에서 활용되는 자격증")
  private String description;

  @Schema(description = "교육공고 관련 이미지 url", example = "...")
  private String s3url;

  @Schema(description = "자격요건", example = "16세 이상 누구나")
  private String requirement;

  @Schema(description = "주무부처", example = "보건복지부")
  private String competentAuthority;

  @Schema(description = "발급기관", example = "대한자격개발검정원")
  private String issuingAuthority;

  @Schema(description = "좋아요 여부", example = "true")
  private Boolean isLiked;
}
