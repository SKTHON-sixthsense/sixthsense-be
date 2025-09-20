package com.skthon.sixthsensebe.domain.jobposting.controller;

import com.skthon.sixthsensebe.domain.jobposting.dto.request.CreateJobPostingRequest;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.EmploymentType;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobCategory;
import com.skthon.sixthsensebe.domain.jobposting.service.JobPostingService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "JobPosting", description = "채용공고 등록 API")
public class JobPostingController {

  private final JobPostingService jobPostingService;

  @PostMapping(value = "/jobposting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "채용 공고 등록", description = "채용공고를 등록하고 등록한 정보를 반환")
  public ResponseEntity<BaseResponse<JobPostingResponse>> createJobPosting(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart(value = "jobposting") @Valid CreateJobPostingRequest request,
      @Parameter(description = "업체 상세 요강 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      @RequestPart(value = "image") MultipartFile file,
      @Parameter(description = "고용 형태") @RequestParam("employment") EmploymentType employmentType,
      @Parameter(description = "모집 직종") @RequestParam("jobcategory") JobCategory jobCategory) {

    JobPostingResponse response = jobPostingService.createJobPosting(request, file, employmentType, jobCategory);

    return ResponseEntity.ok(BaseResponse.success("채용공고가 성공적으로 등록되었습니다.", response));

  }

  @GetMapping("/jobpostings")
  @Operation(summary = "채용 공고 전체 조회", description = "등록된 모든 채용공고를 조회합니다")
  public ResponseEntity<BaseResponse<List<JobPostingResponse>>> getAllJobPostings() {
    List<JobPostingResponse> responses = jobPostingService.getAllJobPostings();
    return ResponseEntity.ok(BaseResponse.success("채용공고 목록을 성공적으로 조회했습니다.", responses));
  }

  @GetMapping("/jobposting/{id}")
  @Operation(summary = "채용 공고 단일 조회", description = "ID로 특정 채용공고를 조회합니다")
  public ResponseEntity<BaseResponse<JobPostingResponse>> getJobPosting(@PathVariable Long id) {
    JobPostingResponse response = jobPostingService.getJobPosting(id);
    return ResponseEntity.ok(BaseResponse.success("채용공고를 성공적으로 조회했습니다.", response));
  }
}
