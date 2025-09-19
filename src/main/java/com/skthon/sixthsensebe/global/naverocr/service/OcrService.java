package com.skthon.sixthsensebe.global.naverocr.service;

import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.config.NaverOcrConfig;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.naverocr.dto.request.ImageRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.request.OcrRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.response.OcrResponse;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

  private final NaverOcrConfig naverOcrConfig;
  private final UserRepository userRepository;
  private final RestTemplate restTemplate; // RestAPI 호출용

  public OcrResponse extractText(Long userId) {

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    try {

      log.info("OCR 처리 시작");

      OcrRequest requestDto =
          createOcrRequest();
    }

  }

  protected OcrRequest createOcrRequest(String s3url, String fileName) {

    // 이미지 파일명으로 부터 확장자를 찾아 저장
    ImageFormat format = ImageFormat.extractImageFormat(fileName);

    // 공식 문서 기준 이미지 요청 방식
    ImageRequest images = ImageRequest.builder()
        .format(format)
        .name(fileName)
        .url(s3url)
        .build();

    // 공식 문서 기준 요청 방식
    return OcrRequest.builder()
        .version("V2")
        .requestId(UUID.randomUUID().toString()) // 임의의 API 호출 UUID
        .timestamp(System.currentTimeMillis()) // 임의의 API 호출 시각
        .lang("ko") // OCR 인식 요청 언어 정보
        .enableTableDetection(false) // 표 형태 제공 (손글씨 사진이기 때문에 표 형식 필요 없음)
        .images(List.of(images)) // JSON Array로 작성, 호출당 1개의 이미지 Array 작성 가능, 이미지 크기: 최대 50MB
        .build();
  }

  // OCR API 호출하여 파싱된 데이터 반환 -> 우선 응답 받고 추후에 후처리!!!!!!!
  protected ResponseEntity<String> callOcrApi(OcrRequest request) throws IOException {
    try {
      // HTTP 헤더 설정 (공식 문서 -> X-OCR-SECRET / Content-Type 2가지 필드 필요
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-OCR-SECRET", naverOcrConfig.getSecretKey());

      // API 요청 엔티티 생성 (OcrRequest DTO와 헤더를 함께 포장해서 전송)
      HttpEntity<OcrRequest> requestEntity = new HttpEntity<>(request, headers);

      log.info("Naver OCR API 호출");

      // 이 요청방식 대로 요청을 보내면 응답을 받을 수 있음 -> 응답을 생성
      ResponseEntity<String> response =
          restTemplate.exchange(
              naverOcrConfig.getInvokeUrl(), // api 엔드포인트
              HttpMethod.POST, // http 메서드
              requestEntity, // 헤더와 생성한 요청
              String.class // 응답 타입
          );

      // 응답에서 텍스트 파싱 (응답을 정리한다고 생각) 하여 반환
      return response;

    } catch (Exception e) {
      log.error("OCR API 호출 실패", e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

  /*// 응답 후처리 로직 (커스텀 dto로)
  protected OcrResponse parseResponse(ResponseEntity<String> response) {

  }*/



}
