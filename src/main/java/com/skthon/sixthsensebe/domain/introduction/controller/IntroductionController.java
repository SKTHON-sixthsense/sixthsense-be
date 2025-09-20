package com.skthon.sixthsensebe.domain.introduction.controller;

import com.skthon.sixthsensebe.domain.introduction.service.UploadService;
import com.skthon.sixthsensebe.global.naverocr.dto.response.OcrResponse;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/introduction")
@Tag(name = "introduction", description = "자기소개서 관련 API")
public class IntroductionController {

  private final UploadService uploadService;

  @Operation(
      summary = "사용자가 업로드한 이미지의 텍스트를 OCR로 추출하는 API",
      description = "업로드한 자기소개서 사진의 텍스트를 OCR로 추출하는 API")
  @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<OcrResponse>> getIntroductionfromOcr(
      @Parameter(description = "업로드할 사진") @RequestParam("image") MultipartFile image,
      @RequestParam Long userId) {

    OcrResponse response = uploadService.getImage(image, userId);

    return ResponseEntity.ok(BaseResponse.success("자기소개서 이미지 텍스트 처리 완료", response));
  }
}
