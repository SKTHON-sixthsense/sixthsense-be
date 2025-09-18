package com.skthon.sixthsensebe.domain.jobapplication.dto.response;

import com.skthon.sixthsensebe.domain.jobapplication.entity.ApplyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "JobPostingResponse DTO", description = "채용공고 등록 정보 응답 반환")
public class ApplyResponse {

  @Schema(description = "지원 상태")
  private ApplyStatus applyStatus; // 지원 완료 응답
}
