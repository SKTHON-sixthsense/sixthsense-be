package com.skthon.sixthsensebe.domain.education.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "EduSummaryResponse DTO", description = "교육공고 요약 응답")
public class EduSummaryResponse {

  @Schema(description = "교육공고 id", example = "1")
  private Long id;

  @Schema(description = "교육공고 제목", example = "간병사")
  private String title;

  @Schema(description = "교육공고 설명", example = "다양한 돌봄 현장에서 활용되는 자격증")
  private String summary;

  @Schema(description = "좋아요 여부", example = "true")
  private Boolean isLiked;
  
  @Schema(description = "교육공고 사진 url", example = "...")
  private String s3url;

  @Schema(description = "찜 여부", example = "true")
  private Boolean isFavorited;

}
