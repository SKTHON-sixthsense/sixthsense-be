package com.skthon.sixthsensebe.domain.jobposting.controller;

import com.skthon.sixthsensebe.domain.jobposting.dto.request.CreateJobPostingRequest;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
      @RequestPart(value = "image") MultipartFile file) {

    JobPostingResponse response = jobPostingService.createJobPosting(request, file);

    return ResponseEntity.ok(BaseResponse.success("채용공고가 성공적으로 등록되었습니다.", response));

  }
}
