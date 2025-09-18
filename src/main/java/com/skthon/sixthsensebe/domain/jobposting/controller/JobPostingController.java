package com.skthon.sixthsensebe.domain.jobposting.controller;

import com.skthon.sixthsensebe.domain.jobposting.dto.request.CreateJobPostingRequest;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.service.JobPostingService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "JobPosting", description = "채용공고 관련 API")
public class JobPostingController {

  private final JobPostingService jobPostingService;

  @PostMapping(value = "/jobposting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "채용 공고 등록", description = "채용공고를 등록하고 등록한 정보를 반환")
  public ResponseEntity<BaseResponse<JobPostingResponse>> createJobPosting(
      @Parameter(description = "업로드할 파일")
      @RequestParam("file") MultipartFile file,
      @Parameter(description = "채용공고 정보")
      @ModelAttribute CreateJobPostingRequest request) {

    JobPostingResponse response = jobPostingService.createJobPosting(request, file);

    return ResponseEntity.ok(BaseResponse.success("채용공고가 성공적으로 등록되었습니다.", response));

  }
}
