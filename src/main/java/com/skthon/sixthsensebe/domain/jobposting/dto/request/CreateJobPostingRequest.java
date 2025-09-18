package com.skthon.sixthsensebe.domain.jobposting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "JobPostingRequest DTO", description = "채용공고를 등록하기 위한 데이터 전송")
public class JobPostingRequest {

  @Schema(description = "공고 제목")
  private String postName;

  @Schema(description = "업체 명")
  private String companyName;

  @Schema(description = "모집 현황")
  private String status;

  @Schema(description = "근무지")
  private Integer workLocation;

  @Schema(description = "급여")
  private String salary;

  @Schema(description = "근무 요일")
  private String workDays;

  @Schema(description = "근무 시간")
  private String workHours;

  @Schema(description = "모집 직종")
  private String jobCategory;

  @Schema(description = "고용 형태 (알바/정직원)")
  private String employmentType;

  @Schema(description = "복리 후생")
  private Integer benefits;

  @Schema(description = "학력 조건")
  private String educationRequirement;

  @Schema(description = "우대 조건")
  private String preferredQualifications;

  @Schema(description = "가게 홈페이지 url")
  private String homepageUrl;


}
