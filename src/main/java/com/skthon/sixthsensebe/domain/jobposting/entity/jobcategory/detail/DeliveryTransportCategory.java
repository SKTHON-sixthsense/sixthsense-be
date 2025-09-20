package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryTransportCategory {

  // 운전 및 배송
  PARCEL_LOADING("택배 상하차"),
  FREIGHT_DRIVER("남품 운전"),
  DELIVERY_DRIVER("배송 운전"),
  SUBSTITUTE_DRIVER("대리 운전");

  @JsonValue
  private final String value;

}
