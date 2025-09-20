package com.skthon.sixthsensebe.domain.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
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
