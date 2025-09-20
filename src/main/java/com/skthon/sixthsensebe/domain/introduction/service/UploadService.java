package com.skthon.sixthsensebe.domain.introduction.service;

import com.skthon.sixthsensebe.domain.introduction.dto.response.IntroductionResponse;
import com.skthon.sixthsensebe.domain.introduction.entity.Introduction;
import com.skthon.sixthsensebe.domain.introduction.repository.IntroductionRepository;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.naverocr.dto.response.OcrResponse;
import com.skthon.sixthsensebe.global.naverocr.dto.response.TextField;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import com.skthon.sixthsensebe.global.naverocr.service.OcrService;
import com.skthon.sixthsensebe.global.s3.PathName;
import com.skthon.sixthsensebe.global.s3.exception.S3ErrorCode;
import com.skthon.sixthsensebe.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

  private final OcrService ocrService;
  private final S3Service s3Service;
  private final IntroductionRepository introductionRepository;
  private final UserRepository userRepository;

  @Transactional
  public OcrResponse getImage(MultipartFile file, Long userId) {

    log.info("이미지 업로드 및 OCR 처리 시작 - userId: {}, fileName: {}", userId, file.getOriginalFilename());

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String s3Url = null;
    try {
      // 1. S3에 새 이미지 업로드
      s3Url = s3Service.uploadFile(PathName.INTRODUCTION, file);
      log.info("S3 업로드 완료 - userId: {}, s3Url: {}", userId, s3Url);

      // 2. 파일 정보 추출
      String fileName = file.getOriginalFilename();
      ImageFormat imageFormat = ImageFormat.extractImageFormat(fileName);

      // 3. 기존 Introduction 업데이트 또는 새로 생성
      Introduction introduction;
      if (user.getIntroduction() != null) {
        // 기존 자기소개서가 있으면 업데이트
        Introduction existingIntroduction = user.getIntroduction();
        String oldS3Url = existingIntroduction.getS3Url();

        log.info("기존 자기소개서 업데이트 - userId: {}, oldS3Url: {}, newS3Url: {}",
                 userId, oldS3Url, s3Url);

        introduction = existingIntroduction.toBuilder()
            .s3Url(s3Url)
            .imageName(fileName)
            .imageFormat(imageFormat)
            .inferResult("PROCESSING") // 처리 중 상태
            .description(null) // 이전 텍스트 초기화
            .build();

        introduction = introductionRepository.save(introduction);

        // 기존 S3 파일 삭제 (선택적)
        try {
          if (oldS3Url != null && !oldS3Url.trim().isEmpty() && !oldS3Url.equals(s3Url)) {
            // s3Service.deleteFile(oldS3Url); // 필요시 주석 해제
            log.info("기존 S3 파일 삭제 예정: {}", oldS3Url);
          }
        } catch (Exception s3DeleteError) {
          log.warn("기존 S3 파일 삭제 실패: {}", oldS3Url, s3DeleteError);
        }

      } else {
        // 기존 자기소개서가 없으면 새로 생성
        log.info("새 자기소개서 생성 - userId: {}, s3Url: {}", userId, s3Url);

        introduction = Introduction.builder()
            .s3Url(s3Url)
            .imageName(fileName)
            .imageFormat(imageFormat)
            .inferResult("PROCESSING") // 처리 중 상태
            .user(user)
            .build();

        introduction = introductionRepository.save(introduction);
        user.setIntroduction(introduction);
        userRepository.save(user);
      }

      log.info("자기소개서 DB 저장 완료 - userId: {}, introductionId: {}", userId, introduction.getId());

      // 5. OCR API 호출 (이제 DB에서 새 이미지 정보를 가져옴)
      log.info("OCR API 호출 시작 - userId: {}", userId);
      OcrResponse ocrApiResponse = ocrService.extractText(userId);
      log.info("OCR API 호출 완료 - userId: {}, inferResult: {}", userId, ocrApiResponse.getInferResult());

      // 6. OCR 결과로 Introduction 업데이트
      if ("SUCCESS".equals(ocrApiResponse.getInferResult())) {
        String extractedText = ocrApiResponse.getFields().stream()
            .map(TextField::getInferText)
            .collect(Collectors.joining(" "))
            .trim();

        introduction = introduction.toBuilder()
            .inferResult(ocrApiResponse.getInferResult())
            .description(extractedText)
            .build();

        introductionRepository.save(introduction);
        log.info("OCR 결과로 Introduction 업데이트 완료 - userId: {}, textLength: {}",
                 userId, extractedText.length());
      }

      // 7. OCR 결과에 추가 정보 설정하여 반환
      ocrApiResponse.setS3Url(s3Url);
      ocrApiResponse.setImageName(fileName);
      ocrApiResponse.setImageFormat(imageFormat);

      return ocrApiResponse;

    } catch (Exception e) {
      log.error("이미지 업로드 및 OCR 처리 실패 - userId: {}, error: {}", userId, e.getMessage(), e);

      // 실패 시 정리 작업
      try {
        // 현재 사용자의 Introduction 상태를 ERROR로 업데이트 (이미 저장된 경우)
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser != null && currentUser.getIntroduction() != null) {
          Introduction failedIntroduction = currentUser.getIntroduction().toBuilder()
              .inferResult("ERROR")
              .build();
          introductionRepository.save(failedIntroduction);
          log.info("Introduction 상태를 ERROR로 업데이트 - userId: {}", userId);
        }

        // 실패한 S3 파일 삭제 (선택적)
        if (s3Url != null && !s3Url.trim().isEmpty()) {
          try {
            // s3Service.deleteFile(s3Url); // 필요시 주석 해제
            log.info("실패한 S3 파일 삭제 예정: {}", s3Url);
          } catch (Exception s3DeleteError) {
            log.warn("실패한 S3 파일 삭제 실패: {}", s3Url, s3DeleteError);
          }
        }

      } catch (Exception cleanupException) {
        log.error("실패 후 정리 작업 중 오류 발생 - userId: {}", userId, cleanupException);
      }

      // 실패 응답 반환
      return OcrResponse.builder()
          .inferResult("ERROR")
          .fields(new ArrayList<>())
          .build();
    }
  }

  public void save(Long userId, OcrResponse ocrResponse) {

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    try {
      // OCR 텍스트 추출
      String extractedText = "";
      if ("SUCCESS".equals(ocrResponse.getInferResult())) {
        extractedText = ocrResponse.getFields().stream()
            .map(TextField::getInferText)
            .collect(Collectors.joining(" "))
            .trim();
      }

      // Introduction 엔티티 생성 및 저장
      Introduction introduction = Introduction.builder()
          .s3Url(ocrResponse.getS3Url())
          .imageName(ocrResponse.getImageName())
          .imageFormat(ocrResponse.getImageFormat())
          .inferResult(ocrResponse.getInferResult())
          .description(extractedText) // 추출된 텍스트 저장
          .user(user)
          .build();

      // 기존 자기소개서가 있다면 삭제 (1:1 관계라면)
      if (user.getIntroduction() != null) {
        Introduction oldIntroduction = user.getIntroduction();
        user.setIntroduction(null);
        userRepository.save(user);
        introductionRepository.delete(oldIntroduction);

        // 기존 S3 파일 삭제 (선택적)
        // s3Service.deleteFile(oldIntroduction.getS3Url());
      }

      introduction = introductionRepository.save(introduction);
      user.setIntroduction(introduction);
      userRepository.save(user);

      log.info("자기소개서 최종 저장 완료 - userId: {}", userId);

    } catch (Exception e) {
      log.error("자기소개서 저장 실패 - userId: {}", userId, e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR); // 적절한 에러코드 사용
    }
  }

  public List<IntroductionResponse> getAllIntroductions() {
    log.info("=== 모든 자기소개서 조회 ===");
    List<Introduction> introductions = introductionRepository.findAll();

    return introductions.stream()
        .map(this::toIntroductionResponse)
        .collect(Collectors.toList());
  }

  public IntroductionResponse getIntroductionByUserId(Long userId) {
    log.info("=== 사용자별 자기소개서 조회 - userId: {} ===", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (user.getIntroduction() == null) {
      return null;
    }

    return toIntroductionResponse(user.getIntroduction());
  }

  private IntroductionResponse toIntroductionResponse(Introduction introduction) {
    return IntroductionResponse.builder()
        .id(introduction.getId())
        .s3Url(introduction.getS3Url())
        .imageName(introduction.getImageName())
        .description(introduction.getDescription())
        .imageFormat(introduction.getImageFormat())
        .inferResult(introduction.getInferResult())
        .userId(introduction.getUser() != null ? introduction.getUser().getId() : null)
        .build();
  }
}
