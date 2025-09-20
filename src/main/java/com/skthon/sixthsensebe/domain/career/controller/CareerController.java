package com.skthon.sixthsensebe.domain.career.controller;

import com.skthon.sixthsensebe.domain.career.dto.request.CareerRequest;
import com.skthon.sixthsensebe.domain.career.dto.response.CareerResponse;
import com.skthon.sixthsensebe.domain.career.entity.Career;
import com.skthon.sixthsensebe.domain.career.mapper.CareerMapper;
import com.skthon.sixthsensebe.domain.career.service.CareerService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/careers")
@Tag(name = "Career", description = "경력(이력) 관리 API")
public class CareerController {

  private final CareerService careerService;
  private final CareerMapper careerMapper;

  @Operation(summary = "특정 사용자의 경력 목록 조회", description = "userId에 해당하는 사용자의 모든 경력을 조회합니다.")
  @GetMapping(value = "/users/{userId}/careers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<CareerResponse>>> getUserCareers(
      @PathVariable Long userId
  ) {
    List<CareerResponse> body = careerService.getMyCareers(userId).stream()
        .map(careerMapper::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(BaseResponse.success(body));
  }

  @Operation(
      summary = "경력 추가",
      description = """
          특정 사용자의 경력을 한 건 추가합니다.  
          - `endDate` 값을 **null** 로 두면 현재 재직중(`isCurrent = true`)으로 처리됩니다.  
          - 날짜를 입력하면 해당 날짜를 종료일로 저장하고 `isCurrent = false`가 됩니다.
          """
  )
  @PostMapping(value = "/users/{userId}/careers",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<CareerResponse>> addCareer(
      @PathVariable Long userId,
      @Valid @RequestBody CareerRequest.Create request
  ) {
    Career created = careerService.addCareer(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success(careerMapper.toResponse(created)));
  }

  @Operation(summary = "경력 삭제", description = "특정 사용자의 경력 한 건을 삭제합니다.")
  @DeleteMapping(value = "/users/{userId}/careers/{careerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<Void>> deleteCareer(
      @PathVariable Long userId,
      @PathVariable Long careerId
  ) {
    careerService.deleteCareer(userId, careerId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }
}
