package com.skthon.sixthsensebe.domain.favorite.repository;

import com.skthon.sixthsensebe.domain.favorite.entity.Favorite;
import com.skthon.sixthsensebe.domain.favorite.entity.FavoriteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  // 사용자가 특정 채용공고에 찜을 했는지 확인
  @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.jobPosting.id = :jobPostingId")
  Optional<Favorite> findByUserIdAndJobPostingId(@Param("userId") Long userId, @Param("jobPostingId") Long jobPostingId);

  // 사용자가 특정 교육공고에 찜을 했는지 확인
  @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.education.id = :educationId")
  Optional<Favorite> findByUserIdAndEducationId(@Param("userId") Long userId, @Param("educationId") Long educationId);

  // 사용자가 찜한 모든 항목 조회 (타입별 필터링 가능)
  @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND (:favoriteType IS NULL OR f.favoriteType = :favoriteType) ORDER BY f.createdAt DESC")
  List<Favorite> findByUserIdAndFavoriteType(@Param("userId") Long userId, @Param("favoriteType") FavoriteType favoriteType);

  // 사용자가 찜한 모든 항목 조회
  @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
  List<Favorite> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}