package com.skthon.sixthsensebe.domain.jobposting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "JobPostingRequest DTO", description = "채용공고를 등록하기 위한 데이터 전송")

public class CreateJobPostingRequest {

  @Schema(description = "공고 제목", example = "주방보조 구합니다")
  private String postName;

  @Schema(description = "업체 명", example = "다시잡")
  private String companyName;

  // 요청 보낸 후는 항상 모집 중인 상태
  /*@Schema(description = "모집 현황", example = " or REGULAR")
  private RecruitmentStatus status;*/

  @Schema(description = "근무지", example = "서울시 은평구")
  private String workLocation;

  @Schema(description = "급여", example = "시급 12000원")
  private String salary;

  @Schema(description = "근무 요일", example = "월화수")
  private String workDays;

  @Schema(description = "근무 시간", example = "협의")
  private String workHours;

  @Schema(description = "모집 직종", example = "주방보조")
  private String jobCategory;

  /*@Schema(description = "고용 형태 (알바/정직원)", example = "PARTTIME")
  private EmploymentType employmentType;*/

  @Schema(description = "복리 후생", example = "식대지원")
  private String benefits;

  @Schema(description = "학력 조건", example = "학력무관")
  private String educationRequirement;

  @Schema(description = "우대 조건", example = "열정적인 사람")
  private String preferredQualifications;

  @Schema(description = "가게 홈페이지 url", example = "www.dasijob.com")
  private String homepageUrl;

  @Schema(description = "가게 전화번호", example = "02-1234-1234")
  private String callNum;

}