package com.skthon.sixthsensebe.domain.search.service;

import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.jobposting.mapper.JobPostingMapper;
import com.skthon.sixthsensebe.domain.jobposting.repository.JobPostingRepository;
import com.skthon.sixthsensebe.domain.search.dto.request.SearchRequest;
import com.skthon.sixthsensebe.domain.search.entity.SearchResult;
import com.skthon.sixthsensebe.domain.search.entity.Seoul;
import com.skthon.sixthsensebe.domain.search.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final JobPostingRepository jobPostingRepository;
  private final JobPostingMapper jobPostingMapper;
  private final SearchResultRepository searchResultRepository;

  public List<JobPostingResponse> searchJobPostings(SearchRequest request) {
    log.info("=== 채용공고 검색 시작 ===");
    log.info("구 필터: {}", request.getDistrict());
    log.info("직종 대분류 필터: {}", request.getJobCategories());
    log.info("직종 세부분류 필터: {}", request.getDetailJobCategories());

    // 전체 채용공고 조회 (생성일시 내림차순 정렬)
    List<JobPosting> allJobPostings = jobPostingRepository.findAllByOrderByCreatedAtDesc();

    // 필터링 적용
    List<JobPosting> filteredJobPostings = allJobPostings.stream()
        .filter(jobPosting -> applyFilters(jobPosting, request))
        .collect(Collectors.toList());

    log.info("필터링 결과: 전체 {}개 중 {}개 매칭", allJobPostings.size(), filteredJobPostings.size());

    // 검색 결과 저장
    saveSearchResult(request, filteredJobPostings);

    // DTO 변환
    return filteredJobPostings.stream()
        .map(jobPostingMapper::toJobPostingResponse)
        .collect(Collectors.toList());
  }

  private boolean applyFilters(JobPosting jobPosting, SearchRequest request) {
    // 1. 서울시 구 필터 (workLocation에서 구 이름이 포함되어 있으면 매칭)
    if (request.getDistrict() != null) {
      // "서울 전체" 선택 시 모든 지역 허용
      if (request.getDistrict() == Seoul.ALL) {
        log.debug("서울 전체 필터 - 모든 지역 허용");
      } else {
        boolean districtMatch = jobPosting.getWorkLocation() != null &&
            jobPosting.getWorkLocation().contains(request.getDistrict().getValue());

        log.debug("구 필터 체크 - 채용공고 근무지: {}, 요청 구: {}, 매칭: {}",
            jobPosting.getWorkLocation(), request.getDistrict(), districtMatch);

        if (!districtMatch) return false;
      }
    }

    // 2. 직종 대분류 필터 (JobCategory enum 직접 비교)
    if (request.getJobCategories() != null && !request.getJobCategories().isEmpty()) {
      boolean categoryMatch = request.getJobCategories().contains(jobPosting.getJobCategory());

      log.debug("대분류 필터 체크 - 채용공고 대분류: {}, 요청 대분류: {}, 매칭: {}",
          jobPosting.getJobCategory(), request.getJobCategories(), categoryMatch);

      if (!categoryMatch) return false;
    }

    // 3. 직종 세부분류 필터 (DetailJobCategory 리스트에서 교집합 확인)
    if (request.getDetailJobCategories() != null && !request.getDetailJobCategories().isEmpty()) {
      if (jobPosting.getDetailJobCategory() == null || jobPosting.getDetailJobCategory().isEmpty()) {
        return false;
      }

      boolean detailCategoryMatch = request.getDetailJobCategories().stream()
          .anyMatch(requestDetail -> jobPosting.getDetailJobCategory().contains(requestDetail));

      log.debug("세부분류 필터 체크 - 채용공고 세부분류: {}, 요청 세부분류: {}, 매칭: {}",
          jobPosting.getDetailJobCategory(), request.getDetailJobCategories(), detailCategoryMatch);

      if (!detailCategoryMatch) return false;
    }

    return true;
  }

  private Integer extractSalaryFromString(String salaryString) {
    if (salaryString == null) return null;

    try {
      // 숫자만 추출 (시급 15000원 -> 15000)
      String numbers = salaryString.replaceAll("[^0-9]", "");
      return numbers.isEmpty() ? null : Integer.parseInt(numbers);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Sort createSort(String sortBy, String sortDirection) {
    Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ?
        Sort.Direction.ASC : Sort.Direction.DESC;

    return Sort.by(direction, sortBy);
  }

  private void saveSearchResult(SearchRequest request, List<JobPosting> filteredJobPostings) {
    List<Long> jobPostingIds = filteredJobPostings.stream()
        .map(JobPosting::getId)
        .collect(Collectors.toList());

    SearchResult searchResult = SearchResult.builder()
        .district(request.getDistrict())
        .jobCategories(request.getJobCategories())
        .detailJobCategories(request.getDetailJobCategories())
        .jobPostingIds(jobPostingIds)
        .resultCount(filteredJobPostings.size())
        .build();

    searchResultRepository.save(searchResult);
    log.info("검색 결과 저장 완료 - ID: {}, 결과 개수: {}", searchResult.getId(), searchResult.getResultCount());
  }

  public List<JobPostingResponse> getSavedSearchResults() {
    log.info("=== 저장된 검색 결과 조회 ===");
    List<SearchResult> searchResults = searchResultRepository.findAllByOrderByCreatedAtDesc();

    if (searchResults.isEmpty()) {
      log.info("저장된 검색 결과가 없습니다.");
      return List.of();
    }

    SearchResult latestSearchResult = searchResults.get(0);

    // 가장 최근 검색 결과의 채용공고 ID 사용
    List<Long> allJobPostingIds = latestSearchResult.getJobPostingIds();

    log.info("가장 최근 검색 결과에서 총 {}개의 채용공고 발견", allJobPostingIds.size());

    // JobPosting 조회 및 DTO 변환
    List<JobPosting> jobPostings = allJobPostingIds.stream()
        .map(jobPostingRepository::findById)
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .collect(Collectors.toList());

    return jobPostings.stream()
        .map(jobPostingMapper::toJobPostingResponse)
        .collect(Collectors.toList());
  }

  public List<JobPostingResponse> getJobPostingsBySearchResult(Long searchResultId) {
    log.info("=== 검색 결과로 채용공고 조회 - ID: {} ===", searchResultId);

    SearchResult searchResult = searchResultRepository.findById(searchResultId)
        .orElseThrow(() -> new IllegalArgumentException("검색 결과를 찾을 수 없습니다: " + searchResultId));

    List<JobPosting> jobPostings = searchResult.getJobPostingIds().stream()
        .map(jobPostingRepository::findById)
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .collect(Collectors.toList());

    log.info("검색 결과로 조회된 채용공고 개수: {}", jobPostings.size());

    return jobPostings.stream()
        .map(jobPostingMapper::toJobPostingResponse)
        .collect(Collectors.toList());
  }

  public List<Seoul> getAllDistricts() {
    return Arrays.asList(Seoul.values());
  }
}
