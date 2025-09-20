package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FoodServiceCategory {

  // 요식업
  DISHWASHER("설거지"),
  KITCHEN_ASSISTANT("주방 보조"),
  HALL_SERVICE("홀 서빙"),
  COFFEE_PREPARATION("커피 제조");

  @JsonValue
  private final String value;
}
