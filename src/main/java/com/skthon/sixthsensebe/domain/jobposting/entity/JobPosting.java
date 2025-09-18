package com.skthon.sixthsensebe.domain.jobposting.entity;

import com.skthon.sixthsensebe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobposting")
public class JobPosting extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "postName")
  private String postName; // 공고 제목

  @Column(name = "companyName")
  private String companyName; // 업체명

  @Column(name = "s3Key")
  private String s3Key; // s3에 업로드한 이미지의 s3key 값

  @Enumerated(EnumType.STRING)
  @Column(name = "recruitmentStatus")
  private RecruitmentStatus status; // 모집 현황

  @Column(name = "workLocation")
  private String workLocation; // 근무지

  @Column(name = "salary")
  private String salary; // 급여

  @Column(name = "workDays")
  private String workDays; // 근무 요일

  @Column(name = "workHour")
  private String workHour; // 근무 시간

  @Column(name = "jobCategory")
  private String jobCategory; // 모집 직종

  @Enumerated(EnumType.STRING)
  @Column(name = "employmentType")
  private EmploymentType employmentType; // 고용 형태 (알바 / 정직원)

  @Column(name = "benefits")
  private String benefits; // 복리 후생

  @Column(name = "educationRequirement")
  private String educationRequirement; // 학력 조건

  @Column(name = "preferredQualifications")
  private String preferredQualifications; // 우대 조건

  @Column(name = "homepageUrl")
  private String homepageUrl; // 가게 홈페이지 url

  @Column(name = "callNum")
  private String callNum; // 가게 전화번호

}
