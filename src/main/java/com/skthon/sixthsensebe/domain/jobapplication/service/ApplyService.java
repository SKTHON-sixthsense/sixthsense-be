package com.skthon.sixthsensebe.domain.jobapplication.service;

import com.skthon.sixthsensebe.domain.jobapplication.dto.response.ApplyResponse;
import com.skthon.sixthsensebe.domain.jobapplication.entity.ApplyStatus;
import com.skthon.sixthsensebe.domain.jobapplication.entity.JobApplication;
import com.skthon.sixthsensebe.domain.jobapplication.repository.JobApplicationRepository;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.jobposting.exception.JobPostingErrorCode;
import com.skthon.sixthsensebe.domain.jobposting.repository.JobPostingRepository;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

  private final UserRepository userRepository;
  private final JobPostingRepository jobPostingRepository;
  private final JobApplicationRepository jobApplicationRepository;

  public ApplyResponse applyJobPost(Long userId, Long jobPostingId) {

    // 사용자(구직자) 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 채용공고 조회
    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new CustomException(JobPostingErrorCode.JOB_POSTING_NOT_FOUND));

    JobApplication jobApplication = JobApplication.builder()
        .jobPosting(jobPosting)
        .user(user)
        .status(ApplyStatus.APPLIED)
        .build();

    // DB 저장
    jobApplicationRepository.save(jobApplication);

    // 응답 생성
    return ApplyResponse.builder()
        .applyStatus(ApplyStatus.APPLIED)
        .build();
  }
}
