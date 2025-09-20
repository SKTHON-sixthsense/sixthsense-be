package com.skthon.sixthsensebe.domain.jobapplication.controller;

import com.skthon.sixthsensebe.domain.jobapplication.dto.response.ApplyResponse;
import com.skthon.sixthsensebe.domain.jobapplication.service.ApplyService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "JobApplication", description = "채용공고 지원 API")
public class JobApplicationController {

  private final ApplyService applyService;

  @PostMapping(value = "/job-applications")
  @Operation(summary = "채용 공고 지원", description = "등록된 채용공고 지원 api")
  public ResponseEntity<BaseResponse<ApplyResponse>> applyJobPosting(
      @RequestParam Long userId,
      @RequestParam Long jobPostingId) {

    ApplyResponse response = applyService.applyJobPost(userId, jobPostingId);

    return ResponseEntity.ok(BaseResponse.success("지원완료.", response));
  }

}
