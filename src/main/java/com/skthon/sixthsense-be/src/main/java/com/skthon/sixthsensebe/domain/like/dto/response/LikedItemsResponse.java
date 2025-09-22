package com.skthon.sixthsensebe.domain.like.dto.response;

import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(title = "LikedItemsResponse DTO", description = "사용자가 좋아요 누른 항목들 응답 DTO")
public class LikedItemsResponse {

  @Schema(description = "좋아요 누른 채용공고 목록")
  private List<JobPostingResponse> jobPostings;

  @Schema(description = "좋아요 누른 교육공고 목록")
  private List<EduSummaryResponse> educations;

  @Schema(description = "전체 좋아요 개수", example = "10")
  private Integer totalCount;
}