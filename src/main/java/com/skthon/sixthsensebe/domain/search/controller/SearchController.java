package com.skthon.sixthsensebe.domain.search.controller;

import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.search.dto.request.SearchRequest;
import com.skthon.sixthsensebe.domain.search.service.SearchService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "Search", description = "채용공고 검색 API")
public class SearchController {

  private final SearchService searchService;

  @PostMapping("/jobpostings")
  @Operation(summary = "채용공고 필터링 검색",
      description = "다양한 필터 조건으로 채용공고를 검색. 지역, 대분류는 단일 선택, 세부 직종은 다중 선택 가능.")
  public ResponseEntity<BaseResponse<List<JobPostingResponse>>> searchJobPostings(
      @RequestBody SearchRequest searchRequest) {

    log.info("=== 채용공고 검색 요청 ===");
    log.info("구 필터: {}", searchRequest.getDistrict());
    log.info("직종 대분류: {}", searchRequest.getJobCategories());
    log.info("직종 세부분류: {}", searchRequest.getDetailJobCategories());

    List<JobPostingResponse> result = searchService.searchJobPostings(searchRequest);

    return ResponseEntity.ok(
        BaseResponse.success("채용공고 검색을 완료했습니다.", result)
    );
  }

  @GetMapping("/results")
  @Operation(summary = "메인페이지에 검색 결과 목록 조회(첫 온보딩 응답 이후 첫 메인페이지 -> 그 이후의 검색 필터는 api/search/jobpostings으로 요청 보내야함)", description = "필터링된 채용공고 리스트를 모두 조회")
  public ResponseEntity<BaseResponse<List<JobPostingResponse>>> getSavedSearchResults() {
    List<JobPostingResponse> results = searchService.getSavedSearchResults();
    return ResponseEntity.ok(BaseResponse.success("저장된 검색 결과를 조회했습니다.", results));
  }

  /*@GetMapping("/results/{searchResultId}/jobpostings")
  @Operation(summary = "검색 결과로 채용공고 조회", description = "저장된 검색 결과 ID로 해당하는 채용공고들을 조회합니다.")
  public ResponseEntity<BaseResponse<List<JobPostingResponse>>> getJobPostingsBySearchResult(
      @PathVariable Long searchResultId) {
    List<JobPostingResponse> jobPostings = searchService.getJobPostingsBySearchResult(searchResultId);
    return ResponseEntity.ok(BaseResponse.success("검색 결과에 해당하는 채용공고를 조회했습니다.", jobPostings));
  }*/

  @GetMapping("/districts")
  @Operation(summary = "서울시 구 목록 조회", description = "검색 필터에서 사용할 수 있는 서울시 구 목록을 조회합니다.")
  public ResponseEntity<BaseResponse<Object>> getDistricts() {
    Object districts = searchService.getAllDistricts();
    return ResponseEntity.ok(BaseResponse.success("서울시 구 목록을 조회했습니다.", districts));
  }
}
