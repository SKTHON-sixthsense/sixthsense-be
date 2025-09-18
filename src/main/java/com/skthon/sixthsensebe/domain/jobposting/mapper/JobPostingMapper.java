package com.skthon.sixthsensebe.domain.jobposting.mapper;

import com.amazonaws.services.s3.AmazonS3;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.global.config.S3Config;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobPostingMapper {

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  // entity -> response dto
  public JobPostingResponse toJobPostingResponse(JobPosting jobPosting) {
    return JobPostingResponse.builder()
        .id(jobPosting.getId())
        .postName(jobPosting.getPostName())
        .s3Url(jobPosting.getS3Url())
        .companyName(jobPosting.getCompanyName())
        .status(jobPosting.getStatus())
        .workLocation(jobPosting.getWorkLocation())
        .salary(jobPosting.getSalary())
        .workDays(jobPosting.getWorkDays())
        .workHours(jobPosting.getWorkHour())
        .jobCategory(jobPosting.getJobCategory())
        .employmentType(jobPosting.getEmploymentType())
        .benefits(jobPosting.getBenefits())
        .educationRequirement(jobPosting.getEducationRequirement())
        .preferredQualifications(jobPosting.getPreferredQualifications())
        .homepageUrl(jobPosting.getHomepageUrl())
        .callNum(jobPosting.getCallNum())
        .build();
  }
}
