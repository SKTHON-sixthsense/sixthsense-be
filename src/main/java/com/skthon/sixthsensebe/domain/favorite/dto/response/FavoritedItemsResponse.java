package com.skthon.sixthsensebe.domain.favorite.dto.response;

import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(title = "FavoritedItemsResponse DTO", description = "사용자가 찜한 항목들 응답 DTO")
public class FavoritedItemsResponse {

  @Schema(description = "찜한 채용공고 목록")
  private List<JobPostingResponse> jobPostings;

  @Schema(description = "찜한 교육공고 목록")
  private List<EduSummaryResponse> educations;
}