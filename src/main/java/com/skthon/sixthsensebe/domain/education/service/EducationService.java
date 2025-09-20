package com.skthon.sixthsensebe.domain.education.service;

import com.skthon.sixthsensebe.domain.education.dto.response.EduResponse;
import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.education.entity.Education;
import com.skthon.sixthsensebe.domain.education.exception.EduErrorCode;
import com.skthon.sixthsensebe.domain.education.repository.EducationRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

  private final EducationRepository educationRepository;

  // 교육공고 요약 전체 조회
  @Transactional
  public List<EduSummaryResponse> getAllEduSummaries() {
    log.info("[EducationService] 교육공고 요약 전체 조회 시도");
    List<Education> educationList = educationRepository.findAll();

    log.info("[EducationService] 교육공고 요약 전체 조회 완료");
    return educationList.stream().map(this::toEduSummaryResponse).toList();
  }

  // 교육공고 단일 조회
  @Transactional
  public EduResponse getEduById(Long id) {
    log.info("[EducationService] 교육공고 단일 조회 시도");
    Education education = educationRepository.findById(id)
        .orElseThrow(() -> new CustomException(EduErrorCode.EDU_NOT_FOUND));
    log.info("[EducationService] 교육공고 단일 조회 완료 educationId = {}", education.getId());
    return toEduResponse(education);
  }

  @Transactional
  public void updateEducationImage(Long id, String s3url) {
    Education education = educationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("교육 정보를 찾을 수 없습니다."));

    education.setS3url(s3url);
    educationRepository.save(education);
  }


  public EduSummaryResponse toEduSummaryResponse(Education education) {
    return EduSummaryResponse.builder()
        .id(education.getId())
        .title(education.getTitle())
        .summary(education.getSummary())
        .isFavorited(true) // 추후 변경 필요
        .build();
  }

  public EduResponse toEduResponse(Education education) {
    return EduResponse.builder()
        .id(education.getId())
        .title(education.getTitle())
        .description(education.getDescription())
        .requirement(education.getRequirement())
        .s3url(education.getS3url())
        .competentAuthority(education.getCompetentAuthority())
        .issuingAuthority(education.getIssuingAuthority())
        .isLiked(true) // 추후 변경 필요
        .build();
  }
}
