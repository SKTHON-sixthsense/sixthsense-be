package com.skthon.sixthsensebe.domain.education.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "education")
public class Education {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "education_id")
  private Long id; // 교육공고 고유 번호

  @Column(nullable = false)
  private String title; // 교육공고 제목

  @Column(nullable = false)
  private String summary; // 교육공고 간단한 설명

  @Column(nullable = true)
  private String s3url; // 교육공고 관련 이미지 url

  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description; // 교육공고 설명

  @Column(nullable = false)
  private String requirement; // 자격요건

  @Column(nullable = false)
  private String competentAuthority;  // 주무부처

  @Column(nullable = false)
  private String issuingAuthority; // 발급기관

  @Column(nullable = false)
  private String homepageUrl; // 교육업체 홈페이지

}
