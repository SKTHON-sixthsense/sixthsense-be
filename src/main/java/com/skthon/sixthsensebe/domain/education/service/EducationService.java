package com.skthon.sixthsensebe.domain.education.service;

import com.skthon.sixthsensebe.domain.education.dto.response.EduResponse;
import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.education.entity.Education;
import com.skthon.sixthsensebe.domain.education.exception.EduErrorCode;
import com.skthon.sixthsensebe.domain.education.repository.EducationRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


  public EduSummaryResponse toEduSummaryResponse(Education education) {
    return EduSummaryResponse.builder()
        .title(education.getTitle())
        .summary(education.getSummary())
        .isLiked(false) // 추후 변경 필요
        .build();
  }

  public EduResponse toEduResponse(Education education) {
    return EduResponse.builder()
        .title(education.getTitle())
        .description(education.getDescription())
        .requirement(education.getRequirement())
        .competentAuthority(education.getCompetentAuthority())
        .issuingAuthority(education.getIssuingAuthority())
        .isLiked(false) // 추후 변경 필요
        .build();
  }
}
