package com.skthon.sixthsensebe.global.naverocr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.config.NaverOcrConfig;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.naverocr.dto.request.ImageRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.request.OcrRequest;
import com.skthon.sixthsensebe.global.naverocr.dto.response.OcrResponse;
import com.skthon.sixthsensebe.global.naverocr.dto.response.TextField;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

  private final NaverOcrConfig naverOcrConfig;
  private final UserRepository userRepository;
  private final RestTemplate restTemplate; // RestAPI 호출용

  @Transactional(readOnly = true)
  public OcrResponse extractText(Long userId) {

    String requestId = UUID.randomUUID().toString();
    long startTime = System.currentTimeMillis();

    // 사용자 조회 - 새로운 트랜잭션에서 최신 데이터 조회
    User user = getFreshUserData(userId);

    try {

      String s3Url = user.getIntroduction().getS3Url();
      String imageName = user.getIntroduction().getImageName();
      String currentInferResult = user.getIntroduction().getInferResult();

      log.info("OCR 처리 시작 - userId: {}, requestId: {}, imageName: {}, s3Url: {}, 기존 inferResult: {}",
          userId, requestId, imageName, s3Url, currentInferResult);

      // 이미지 정보 검증
      if (s3Url == null || s3Url.trim().isEmpty()) {
        log.error("S3 URL이 없습니다. userId: {}, requestId: {}", userId, requestId);
        throw new IllegalStateException("이미지 URL이 없습니다.");
      }

      if (imageName == null || imageName.trim().isEmpty()) {
        log.error("이미지 이름이 없습니다. userId: {}, requestId: {}", userId, requestId);
        throw new IllegalStateException("이미지 이름이 없습니다.");
      }

      OcrRequest requestDto = createOcrRequest(s3Url, imageName, requestId);

      // Ocr API 호출
      OcrResponse ocrResult = callOcrApi(requestDto, requestId); // 추후에 OcrResponse로 변경 (덱스트 후처리 후)

      long endTime = System.currentTimeMillis();
      log.info("OCR 처리 완료 - userId: {}, requestId: {}, 소요시간: {}ms, 텍스트 필드 수: {}",
          userId, requestId, (endTime - startTime), ocrResult.getFields().size());

      return ocrResult;

    } catch (IOException e) { // callOcrApi()에서 IOException을 던지고 있기 때문에 받아서 다시 던져야 함
      long endTime = System.currentTimeMillis();
      log.error("OCR 처리 실패 - userId: {}, requestId: {}, 소요시간: {}ms, error: {}",
          userId, requestId, (endTime - startTime), e.getMessage(), e);
      throw new RuntimeException(e);
    }

  }

  protected OcrRequest createOcrRequest(String s3url, String fileName, String requestId) {

    log.debug("OCR 요청 생성 - requestId: {}, fileName: {}, s3url: {}", requestId, fileName, s3url);

    // 이미지 파일명으로 부터 확장자를 찾아 저장
    ImageFormat format = ImageFormat.extractImageFormat(fileName);

    // 공식 문서 기준 이미지 요청 방식
    ImageRequest images = ImageRequest.builder()
        .format(format)
        .name(fileName)
        .url(s3url)
        .build();

    // 공식 문서 기준 요청 방식 - 전달받은 requestId 사용으로 고유성 보장
    return OcrRequest.builder()
        .version("V2")
        .requestId(requestId) // 메소드 파라미터로 전달받은 고유 requestId 사용
        .timestamp(System.currentTimeMillis()) // 임의의 API 호출 시각
        .lang("ko") // OCR 인식 요청 언어 정보
        .enableTableDetection(false) // 표 형태 제공 (손글씨 사진이기 때문에 표 형식 필요 없음)
        .images(List.of(images)) // JSON Array로 작성, 호출당 1개의 이미지 Array 작성 가능, 이미지 크기: 최대 50MB
        .build();
  }

  // OCR API 호출하여 파싱된 데이터 반환 -> 우선 응답 받고 추후에 후처리!!!!!!!
  protected OcrResponse callOcrApi(OcrRequest request, String requestId) throws IOException {
    long apiStartTime = System.currentTimeMillis();

    try {
      // HTTP 헤더 설정 (공식 문서 -> X-OCR-SECRET / Content-Type 2가지 필드 필요
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-OCR-SECRET", naverOcrConfig.getSecretKey());

      // API 요청 엔티티 생성 (OcrRequest DTO와 헤더를 함께 포장해서 전송)
      HttpEntity<OcrRequest> requestEntity = new HttpEntity<>(request, headers);

      log.info("Naver OCR API 호출 시작 - requestId: {}", requestId);

      // 이 요청방식 대로 요청을 보내면 응답을 받을 수 있음 -> 응답을 생성
      ResponseEntity<String> response =
          restTemplate.exchange(
              naverOcrConfig.getInvokeUrl(), // api 엔드포인트
              HttpMethod.POST, // http 메서드
              requestEntity, // 헤더와 생성한 요청
              String.class // 응답 타입
          );

      long apiEndTime = System.currentTimeMillis();
      log.info("Naver OCR API 호출 완료 - requestId: {}, HTTP 상태: {}, API 응답시간: {}ms",
          requestId, response.getStatusCode(), (apiEndTime - apiStartTime));

      // 응답에서 텍스트 파싱 (응답을 정리한다고 생각) 하여 반환
      return parseOcrResponse(response, requestId);

    } catch (Exception e) {
      long apiEndTime = System.currentTimeMillis();
      log.error("OCR API 호출 실패 - requestId: {}, API 응답시간: {}ms, error: {}",
          requestId, (apiEndTime - apiStartTime), e.getMessage(), e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

  private OcrResponse parseOcrResponse(ResponseEntity<String> apiResponse, String requestId) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(apiResponse.getBody());
      JsonNode imagesNode = rootNode.path("images");

      if (imagesNode.isArray() && !imagesNode.isEmpty()) {
        // 첫 번째 이미지의 결과만 처리
        JsonNode firstImage = imagesNode.get(0);

        String inferResult = firstImage.path("inferResult").asText();
        List<TextField> textFields = extractTextFields(firstImage, requestId);

        log.info("OCR 응답 파싱 완료 - requestId: {}, inferResult: {}, 추출된 텍스트 필드 수: {}",
            requestId, inferResult, textFields.size());

        return OcrResponse.builder()
            .inferResult(inferResult)
            .fields(textFields)
            .build();
      } else {
        log.warn("OCR 응답에서 이미지를 찾을 수 없음 - requestId: {}", requestId);
        return createFailureResponse();
      }

    } catch (Exception e) {
      log.error("OCR 응답 파싱 실패 - requestId: {}, error: {}", requestId, e.getMessage(), e);
      return createFailureResponse();
    }
  }

  /**
   * fields 배열에서 TextField 리스트 추출
   */
  private List<TextField> extractTextFields(JsonNode imageNode, String requestId) {
    List<TextField> textFields = new ArrayList<>();
    JsonNode fieldsNode = imageNode.path("fields");
    int emptyTextCount = 0;

    if (fieldsNode.isArray()) {
      log.debug("텍스트 필드 추출 시작 - requestId: {}, 전체 필드 수: {}", requestId, fieldsNode.size());

      for (JsonNode fieldNode : fieldsNode) {
        String inferText = fieldNode.path("inferText").asText();
        Double inferConfidence = fieldNode.path("inferConfidence").asDouble(0.0);

        // 빈 텍스트는 제외
        if (inferText != null && !inferText.trim().isEmpty()) {
          TextField textField = TextField.builder()
              .inferText(inferText)
              .build();
          textFields.add(textField);

          log.debug("텍스트 필드 추출 - requestId: {}, text: [{}], confidence: {}",
              requestId, inferText.length() > 50 ? inferText.substring(0, 50) + "..." : inferText,
              inferConfidence);
        } else {
          emptyTextCount++;
        }
      }

      log.info("텍스트 필드 추출 완료 - requestId: {}, 유효 필드: {}, 빈 필드: {}, 전체 필드: {}",
          requestId, textFields.size(), emptyTextCount, fieldsNode.size());
    }

    return textFields;
  }

  private OcrResponse createFailureResponse() {
    return OcrResponse.builder()
        .inferResult("ERROR")
        .fields(new ArrayList<>())
        .build();
  }

  /**
   * 새로운 트랜잭션에서 최신 사용자 데이터 조회
   * JPA 1차 캐시를 우회하여 데이터베이스에서 직접 조회
   */
  @Transactional(readOnly = true)
  protected User getFreshUserData(Long userId) {
    log.debug("최신 사용자 데이터 조회 - userId: {}", userId);

    return userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자를 찾을 수 없습니다. userId: {}", userId);
          return new CustomException(UserErrorCode.USER_NOT_FOUND);
        });
  }

}
