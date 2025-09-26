package com.skthon.sixthsensebe.domain.favorite.service;

import com.skthon.sixthsensebe.domain.education.dto.response.EduSummaryResponse;
import com.skthon.sixthsensebe.domain.education.entity.Education;
import com.skthon.sixthsensebe.domain.education.repository.EducationRepository;
import com.skthon.sixthsensebe.domain.education.service.EducationService;
import com.skthon.sixthsensebe.domain.favorite.dto.response.FavoritedItemsResponse;
import com.skthon.sixthsensebe.domain.favorite.entity.Favorite;
import com.skthon.sixthsensebe.domain.favorite.entity.FavoriteType;
import com.skthon.sixthsensebe.domain.favorite.repository.FavoriteRepository;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.jobposting.mapper.JobPostingMapper;
import com.skthon.sixthsensebe.domain.jobposting.repository.JobPostingRepository;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final JobPostingRepository jobPostingRepository;
  private final EducationRepository educationRepository;
  private final JobPostingMapper jobPostingMapper;
  private final EducationService educationService;

  public boolean toggleJobPostingFavorite(Long userId, Long jobPostingId) {
    log.info("=== 채용공고 찜 토글 - userId: {}, jobPostingId: {} ===", userId, jobPostingId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
        .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다: " + jobPostingId));

    Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndJobPostingId(userId, jobPostingId);

    if (existingFavorite.isPresent()) {
      favoriteRepository.delete(existingFavorite.get());
      log.info("채용공고 찜 취소 - userId: {}, jobPostingId: {}", userId, jobPostingId);
      return false;
    } else {
      Favorite newFavorite = Favorite.builder()
          .user(user)
          .jobPosting(jobPosting)
          .favoriteType(FavoriteType.JOB_POSTING)
          .build();
      favoriteRepository.save(newFavorite);
      log.info("채용공고 찜 추가 - userId: {}, jobPostingId: {}", userId, jobPostingId);
      return true;
    }
  }

  public boolean toggleEducationFavorite(Long userId, Long educationId) {
    log.info("=== 교육공고 찜 토글 - userId: {}, educationId: {} ===", userId, educationId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    Education education = educationRepository.findById(educationId)
        .orElseThrow(() -> new IllegalArgumentException("교육공고를 찾을 수 없습니다: " + educationId));

    Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndEducationId(userId, educationId);

    if (existingFavorite.isPresent()) {
      favoriteRepository.delete(existingFavorite.get());
      log.info("교육공고 찜 취소 - userId: {}, educationId: {}", userId, educationId);
      return false;
    } else {
      Favorite newFavorite = Favorite.builder()
          .user(user)
          .education(education)
          .favoriteType(FavoriteType.EDUCATION)
          .build();
      favoriteRepository.save(newFavorite);
      log.info("교육공고 찜 추가 - userId: {}, educationId: {}", userId, educationId);
      return true;
    }
  }

  @Transactional(readOnly = true)
  public FavoritedItemsResponse getUserFavoritedItems(Long userId, FavoriteType favoriteType) {
    log.info("=== 사용자 찜 목록 조회 - userId: {}, favoriteType: {} ===", userId, favoriteType);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<Favorite> favorites = favoriteRepository.findByUserIdAndFavoriteType(userId, favoriteType);

    List<JobPostingResponse> jobPostings = favorites.stream()
        .filter(favorite -> favorite.getFavoriteType() == FavoriteType.JOB_POSTING)
        .map(favorite -> jobPostingMapper.toJobPostingResponse(favorite.getJobPosting()))
        .collect(Collectors.toList());

    List<EduSummaryResponse> educations = favorites.stream()
        .filter(favorite -> favorite.getFavoriteType() == FavoriteType.EDUCATION)
        .map(favorite -> educationService.toEduSummaryResponse(favorite.getEducation()))
        .collect(Collectors.toList());

    return FavoritedItemsResponse.builder()
        .jobPostings(jobPostings)
        .educations(educations)
        .build();
  }

  /*@Transactional(readOnly = true)
  public boolean isJobPostingFavoritedByUser(Long userId, Long jobPostingId) {
    return favoriteRepository.findByUserIdAndJobPostingId(userId, jobPostingId).isPresent();
  }

  @Transactional(readOnly = true)
  public boolean isEducationFavoritedByUser(Long userId, Long educationId) {
    return favoriteRepository.findByUserIdAndEducationId(userId, educationId).isPresent();
  }*/
}