package com.skthon.sixthsensebe.domain.jobposting.mapper;

import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import org.springframework.stereotype.Component;

@Component
public class JobPostingMapper {

  // entity -> response dto
  public JobPostingResponse toJobPostingResponse(JobPosting jobPosting) {
    return JobPostingResponse.builder()
        .id(jobPosting.getId())
        .postName(jobPosting.getPostName())
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
        .homepageUrl(jobPosting.getHomepageUrl())
        .callNum(jobPosting.getCallNum())
        .build();
  }
}
