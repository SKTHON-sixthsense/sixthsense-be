package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayLaborCategory {

  // 일용직
  CONSTRUCTION_ASSISTANT("공사장 보조"),
  PACKAGING_INSPECTOR("포장 검수"),
  TASTING_GUIDE_DAY("시식 안내"),
  FARM_WORK("농촌 일손");

  @JsonValue
  private final String value;

}
