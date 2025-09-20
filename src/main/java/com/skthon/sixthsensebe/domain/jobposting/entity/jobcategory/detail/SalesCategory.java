package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SalesCategory {

  // 판매직
  CASHIER("계산"), // 계산
  PRODUCT_DISPLAY("상품 진열"),
  TASTING_GUIDE("시식 안내"),
  SALES_ASSISTANT("판매 보조");

  @JsonValue
  private final String value;

}
