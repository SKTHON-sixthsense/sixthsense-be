package com.skthon.sixthsensebe.domain.education.controller;

import com.skthon.sixthsensebe.domain.education.dto.response.EduResponse;
import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.education.service.EducationService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import com.skthon.sixthsensebe.global.s3.PathName;
import com.skthon.sixthsensebe.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "edu", description = "교육공고 관련 API")
public class EducationController {

  private final EducationService educationService;
  private final S3Service s3Service;

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

  @Operation(summary = "더미 교육공고에 올릴 사진 업로드 API", description = "사진 업로드 되면 url필드 업데이드 해주는 API")
  @PostMapping(value = "/educations/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<EduResponse>> uploadImage(
      @Parameter(description = "교육공고 관련 사진") @RequestParam("image") MultipartFile image,
      @RequestParam Long id) {
    EduResponse eduResponse = educationService.getEduById(id);

    // s3 업로드
    String s3url = s3Service.uploadFile(PathName.EDUCATION, image);

    EduResponse updatedResponse = eduResponse.toBuilder()
        .s3url(s3url)
        .build();

    educationService.updateEducationImage(id, s3url);

    return ResponseEntity.ok(BaseResponse.success("교육공고 단일 조회 성공", updatedResponse));
  }

}
