package com.skthon.sixthsensebe.domain.education.controller;

import com.skthon.sixthsensebe.domain.education.dto.response.EduResponse;
import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.education.service.EducationService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "edu", description = "교육공고 관련 API")
public class EducationController {

  private final EducationService educationService;

  @Operation(summary = "교육공고 요약 전체 조회 API", description = "하단의 교육페이지 버튼을 눌렀을때 호출되는 API")
  @GetMapping("/educations")
  public ResponseEntity<BaseResponse<List<EduSummaryResponse>>> getAllEduSummaries() {
    List<EduSummaryResponse> summaryList = educationService.getAllEduSummaries();
    return ResponseEntity.ok(BaseResponse.success("교육공고 요약 전체 조회 성공", summaryList));
  }

  @Operation(summary = "교육공고 단일 조회 API", description = "교육공고 요약 목록들 중 하나를 눌렀을때 호출되는 API")
  @GetMapping("/educations/{Id}")
  public ResponseEntity<BaseResponse<EduResponse>> getEduById(
      @Parameter(description = "조회할 교육공고 ID") @PathVariable Long Id) {
    EduResponse eduResponse = educationService.getEduById(Id);
    return ResponseEntity.ok(BaseResponse.success("교육공고 단일 조회 성공", eduResponse));
  }

}
