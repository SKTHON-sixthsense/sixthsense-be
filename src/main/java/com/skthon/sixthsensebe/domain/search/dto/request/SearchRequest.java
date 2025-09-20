package com.skthon.sixthsensebe.domain.search.dto.request;

import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.DetailJobCategory;
import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.JobCategory;
import com.skthon.sixthsensebe.domain.search.entity.Seoul;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(title = "SearchRequest DTO", description = "채용공고 검색을 위한 필터 조건 (지역은 단일 선택, 직종은 다중 선택 가능)")
public class SearchRequest {

  @Schema(description = "서울시 구 필터 (단일 선택)", example = "강남구", required = false)
  private Seoul district;

  @Schema(description = "직종 대분류 필터 (다중 선택 가능)", required = false)
  private List<JobCategory> jobCategories;

  @Schema(description = "직종 세부분류 필터 (다중 선택 가능)", required = false)
  private List<DetailJobCategory> detailJobCategories;
}
