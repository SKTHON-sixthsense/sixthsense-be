package com.skthon.sixthsensebe.domain.favorite.controller;

import com.skthon.sixthsensebe.domain.favorite.dto.response.FavoritedItemsResponse;
import com.skthon.sixthsensebe.domain.favorite.entity.FavoriteType;
import com.skthon.sixthsensebe.domain.favorite.service.FavoriteService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
@Tag(name = "Favorite", description = "찜 관련 API")
public class FavoriteController {

  private final FavoriteService favoriteService;

  @PostMapping("/jobposting/{jobPostingId}")
  @Operation(summary = "채용공고 찜 토글", description = "채용공고를 찜에 추가하거나 취소")
  public ResponseEntity<BaseResponse<Boolean>> toggleJobPostingFavorite(
      @PathVariable Long jobPostingId,
      @Parameter(description = "사용자 ID") @RequestParam Long userId) {

    boolean isFavorited = favoriteService.toggleJobPostingFavorite(userId, jobPostingId);
    String message = isFavorited ? "채용공고를 찜에 추가했습니다" : "채용공고를 찜에서 취소했습니다";

    return ResponseEntity.ok(BaseResponse.success(message, isFavorited));
  }

  @PostMapping("/education/{educationId}")
  @Operation(summary = "교육공고 찜 토글", description = "교육공고를 찜에 추가하거나 취소합")
  public ResponseEntity<BaseResponse<Boolean>> toggleEducationFavorite(
      @PathVariable Long educationId,
      @Parameter(description = "사용자 ID") @RequestParam Long userId) {

    boolean isFavorited = favoriteService.toggleEducationFavorite(userId, educationId);
    String message = isFavorited ? "교육공고를 찜에 추가" : "교육공고를 찜에서 취소";

    return ResponseEntity.ok(BaseResponse.success(message, isFavorited));
  }

  @GetMapping("/user/{userId}")
  @Operation(summary = "사용자가 찜한 항목 조회", description = "사용자가 찜한 모든 항목을 조회")
  public ResponseEntity<BaseResponse<FavoritedItemsResponse>> getUserFavoritedItems(
      @PathVariable Long userId,
      @Parameter(description = "찜 타입 필터") @RequestParam FavoriteType favoriteType) {

    FavoritedItemsResponse response = favoriteService.getUserFavoritedItems(userId, favoriteType);
    return ResponseEntity.ok(BaseResponse.success("사용자 찜 목록을 조회", response));
  }

 /* @GetMapping("/user/{userId}/jobpostings")
  @Operation(summary = "사용자가 찜한 채용공고 조회", description = "사용자가 찜한 채용공고만 조회합니다")
  public ResponseEntity<BaseResponse<FavoritedItemsResponse>> getUserFavoritedJobPostings(
      @PathVariable Long userId) {

    FavoritedItemsResponse response = favoriteService.getUserFavoritedItems(userId, FavoriteType.JOB_POSTING);
    return ResponseEntity.ok(BaseResponse.success("사용자가 찜한 채용공고를 조회했습니다", response));
  }

  @GetMapping("/user/{userId}/educations")
  @Operation(summary = "사용자가 찜한 교육공고 조회", description = "사용자가 찜한 교육공고만 조회합니다")
  public ResponseEntity<BaseResponse<FavoritedItemsResponse>> getUserFavoritedEducations(
      @PathVariable Long userId) {

    FavoritedItemsResponse response = favoriteService.getUserFavoritedItems(userId, FavoriteType.EDUCATION);
    return ResponseEntity.ok(BaseResponse.success("사용자가 찜한 교육공고를 조회했습니다", response));
  }*/
}