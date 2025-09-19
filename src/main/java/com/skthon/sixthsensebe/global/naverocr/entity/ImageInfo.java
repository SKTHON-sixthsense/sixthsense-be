package com.skthon.sixthsensebe.global.naverocr.entity;

import com.skthon.sixthsensebe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "imageinfo")
public class ImageInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "s3_url", nullable = false)
  private String s3Url;

  @Column(name = "image_name", nullable = false)
  private String imageName;

  @Enumerated(EnumType.STRING)
  @Column(name = "image_format", nullable = false)
  private ImageFormat imageFormat;

  @Column(name = "infer_confidence", nullable = false)
  private Double inferConfidence;

  @Column(name = "infer_result", nullable = false)
  private String inferResult;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
