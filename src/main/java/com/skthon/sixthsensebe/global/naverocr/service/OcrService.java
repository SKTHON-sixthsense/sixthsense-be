package com.skthon.sixthsensebe.global.naverocr.service;

import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.naverocr.dto.request.ImageRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.request.OcrRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.response.OcrResponse;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

  private final UserRepository userRepository;

  public OcrResponse extract(Long userId) {

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    try {

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

}
