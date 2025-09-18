package com.skthon.sixthsensebe.domain.jobposting.service;

import com.skthon.sixthsensebe.domain.jobposting.dto.request.CreateJobPostingRequest;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.jobposting.entity.RecruitmentStatus;
import com.skthon.sixthsensebe.domain.jobposting.exception.JobPostingErrorCode;
import com.skthon.sixthsensebe.domain.jobposting.mapper.JobPostingMapper;
import com.skthon.sixthsensebe.domain.jobposting.repository.JobPostingRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.s3.PathName;
import com.skthon.sixthsensebe.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingService {

  private final JobPostingRepository jobPostingRepository;
  private final JobPostingMapper jobPostingMapper;
  private final S3Service s3Service;

  @Transactional
  public JobPostingResponse createJobPosting(CreateJobPostingRequest request, MultipartFile file) {

    log.info("=== 요청 데이터 확인 ===");
    log.info("postName: {}", request.getPostName());
    log.info("preferredQualifications: {}", request.getPreferredQualifications());
    log.info("benefits: {}", request.getBenefits());
    log.info("workHours: {}", request.getWorkHours());

    try {
      // request 기반 엔티티 생성
      JobPosting jobPosting = JobPosting.builder()
          .postName(request.getPostName())
          .companyName(request.getCompanyName())
          .status(RecruitmentStatus.RECRUITING) // 채용공고 등록 시 모집중 상태 고정
          .workLocation(request.getWorkLocation())
          .salary(request.getSalary())
          .workDays(request.getWorkDays())
          .workHour(request.getWorkHours())
          .jobCategory(request.getJobCategory())
          .employmentType(request.getEmploymentType())
          .benefits(request.getBenefits())
          .educationRequirement(request.getEducationRequirement())
          .preferredQualifications(request.getPreferredQualifications())
          .homepageUrl(request.getHomepageUrl())
          .callNum(request.getCallNum())
          .build();

      log.info("채용공고 정보 생성 완료");

      // 이미지 업로드 및 s3Key 필드 업데이트
      JobPosting jobPostingWithImage = s3Service.uploadImage(
          PathName.JOBPOSTING,
          file,
          jobPosting,
          (s3Url) -> jobPosting.toBuilder().s3Url(s3Url).build()
      );

      log.info("S3로 채용공고 이미지 업로드 완료");

      JobPosting savedJobPosting = jobPostingRepository.save(jobPostingWithImage); // 엔티티 DB 저장

      log.info("채용공고 DB에 저장완료. 채용공고 등록 완료.");

      return jobPostingMapper.toJobPostingResponse(savedJobPosting);
    } catch (Exception e) {
      log.error("채용공고 등록 실패: {}", e.getMessage(), e);
      throw new CustomException(JobPostingErrorCode.JOB_POSTING_CREATE_FAILED);
    }

  }

}
