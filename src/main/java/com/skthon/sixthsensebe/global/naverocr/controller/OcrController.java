package com.skthon.sixthsensebe.global.naverocr.controller;

import com.skthon.sixthsensebe.global.naverocr.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
@Tag(name = "Ocr", description = "Ocr 관련 API")
public class OcrController {

  private final OcrService ocrService;

  @Operation(
      summary = "사용자가 업로드한 이미지의 텍스트를 OCR로 추출하는 API",
      description = "업로드한 자기소개서 사진의 텍스트를 OCR로 추출하는 API")
  @PostMapping("/{userId}")
  public ResponseEntity<ResponseEntity<String>> testOcr(@PathVariable Long userId) {
    ResponseEntity<String> response = ocrService.extractText(userId);
    return ResponseEntity.ok(response);
  }
}
