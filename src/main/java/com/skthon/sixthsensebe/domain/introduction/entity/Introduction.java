package com.skthon.sixthsensebe.domain.introduction.entity;

import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "introduction")
public class Introduction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "s3_url")
  private String s3Url; // s3 url (이미지 url -> ocr 호출을 위해 필요)

  @Column(name = "image_name")
  private String imageName; // 이미지 이름(파일명), 이 필드 지우고 s3 url로 파일명 그냥 못가져오나

  @Column(name = "description")
  private String description; // 자기소개서 내용

  @Enumerated(EnumType.STRING)
  @Column(name = "image_format")
  private ImageFormat imageFormat; // 파일 확장자

  @Column(name = "infer_confidence")
  private Double inferConfidence; // 텍스트 추출 신뢰도

  @Column(name = "infer_result")
  private String inferResult; // 텍스트 추출 결

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

}
