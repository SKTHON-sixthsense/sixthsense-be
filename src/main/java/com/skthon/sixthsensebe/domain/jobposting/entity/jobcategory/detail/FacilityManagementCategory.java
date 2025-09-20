package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FacilityManagementCategory {

  SECURITY_SAFETY("경비 및 보안"),
  PARKING_MANAGEMENT("주차관리 및 안내"),
  CLEANING_BEAUTY("청소 및 미화"),
  STORE_MANAGEMENT("매장 관리");

  @JsonValue
  private final String value;


}
