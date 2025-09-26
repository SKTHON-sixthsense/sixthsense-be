package com.skthon.sixthsensebe.domain.user.entity;

import com.skthon.sixthsensebe.domain.career.entity.Career;
import com.skthon.sixthsensebe.domain.introduction.entity.Introduction;
import com.skthon.sixthsensebe.domain.jobapplication.entity.JobApplication;
import com.skthon.sixthsensebe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

  @Lob
  @Column(name = "s3url")
  private String s3url;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;  // OWNER / WORKER

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 10)
  private Gender gender;

  @Column(name = "phone", length = 20, unique = true)
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "personality")
  private List<Personality> personalityList = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "health")
  private List<Health> healthList = new ArrayList<>();


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Career> experiences = new ArrayList<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Introduction introduction;

  // 사용자는 여러개의 채용공고를 지원할 수 있음
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobApplication> jobApplications = new ArrayList<>();
}
