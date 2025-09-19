package com.skthon.sixthsensebe.domain.user.entity;

import com.skthon.sixthsensebe.domain.jobapplication.entity.JobApplication;
import com.skthon.sixthsensebe.global.common.BaseTimeEntity;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "username", nullable = false, unique = true, length = 30)
  private String username;

  @Column(name = "password", nullable = false, length = 100)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private ImageInfo imageInfo;

  // 사용자는 여러개의 채용공고를 지원할 수 있음
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobApplication> jobApplications = new ArrayList<>();
}
