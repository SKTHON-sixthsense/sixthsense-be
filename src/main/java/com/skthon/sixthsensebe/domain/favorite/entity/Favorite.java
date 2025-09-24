package com.skthon.sixthsensebe.domain.favorite.entity;

import com.skthon.sixthsensebe.domain.education.entity.Education;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.user.entity.User;
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
@Table(name = "favorites")
public class Favorite extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_posting_id")
  private JobPosting jobPosting;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "education_id")
  private Education education;

  @Enumerated(EnumType.STRING)
  @Column(name = "favorite_type", nullable = false)
  private FavoriteType favoriteType;

  @PrePersist
  @PreUpdate
  private void validateFavoriteTarget() {
    int targetCount = 0;
    if (jobPosting != null) targetCount++;
    if (education != null) targetCount++;

    if (targetCount != 1) {
      throw new IllegalStateException("Favorite entity must have exactly one target (JobPosting or Education)");
    }
  }
}