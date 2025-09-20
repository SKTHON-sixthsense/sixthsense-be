package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobCategory {

  // 시설관리 및 운영
  FACILITY_MANAGEMENT("시설관리 및 운영"),

  // 요식업
  FOOD_SERVICE("요식업"),

  // 운전 및 배송
  DELIVERY_TRANSPORT("운전 및 배송"),

  // 판매직
  SALES("판매직"),

  // 가사 및 돌봄
  CARE_SERVICE("가사 및 돌봄"),

  // 일용직
  DAY_LABOR("일용직");

  @JsonValue
  private final String value;

  @JsonCreator
  public static JobCategory fromValue(String value) {
    for (JobCategory category : values()) {
      if (category.getValue().equals(value)) {
        return category;
      }
    }
    throw new IllegalArgumentException("Unknown job category: " + value);
  }
}
