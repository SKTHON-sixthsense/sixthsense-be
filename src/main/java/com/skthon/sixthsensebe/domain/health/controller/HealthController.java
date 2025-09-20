package com.skthon.sixthsensebe.domain.health.controller;

import com.skthon.sixthsensebe.domain.health.service.HealthService;
import com.skthon.sixthsensebe.domain.user.dto.response.TagDto;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "건강 관련 코드 API")
@RestController
@RequiredArgsConstructor
public class HealthController {

  private final HealthService healthService;

  @Operation(summary = "건강 코드 목록 조회", description = "사용자 건강 상태 선택용 코드 목록을 반환합니다.")
  @GetMapping("/api/health")
  public ResponseEntity<BaseResponse<List<TagDto>>> getHealthList() {
    return ResponseEntity.ok(BaseResponse.success(healthService.getAll()));
  }
}
