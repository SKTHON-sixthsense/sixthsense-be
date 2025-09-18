package com.skthon.sixthsensebe.domain.jobposting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "JobPostingResponse DTO", description = "채용공고 등록 정보 응답 반환")
public class JobPostingResponse {
  @Schema(description = "채용공고 식별자")
  private Long id;

  @Schema(description = "공고 제목")
  private String postName;

  @Schema(description = "업체 명")
  private String companyName;

  @Schema(description = "모집 현황")
  private String status;

  @Schema(description = "근무지")
  private String workLocation;

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
  private String benefits;

  @Schema(description = "학력 조건")
  private String educationRequirement;

  @Schema(description = "우대 조건")
  private String preferredQualifications;

  @Schema(description = "가게 홈페이지 url")
  private String homepageUrl;

  @Schema(description = "가게 전화번호")
  private String callNum;
}
