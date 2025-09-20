package com.skthon.sixthsensebe.domain.jobposting.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobCategory {
  // 시설관리 및 운영
  SECURITY_SAFETY("경비 및 보안"),
  PARKING_MANAGEMENT("주차관리 및 안내"),
  CLEANING_BEAUTY("청소 및 미화"),
  STORE_MANAGEMENT("매장 관리"),

  // 요식업
  DISHWASHER("설거지"),
  KITCHEN_ASSISTANT("주방 보조"),
  HALL_SERVICE("홀 서빙"),
  COFFEE_PREPARATION("커피 제조"),

  // 운전 및 배송
  PARCEL_LOADING("택배 상하차"),
  FREIGHT_DRIVER("남품 운전"),
  DELIVERY_DRIVER("배송 운전"),
  SUBSTITUTE_DRIVER("대리 운전"),

  // 판매직
  CASHIER("계산"), // 계산
  PRODUCT_DISPLAY("상품 진열"),
  TASTING_GUIDE("시식 안내"),
  SALES_ASSISTANT("판매 보조"),

  // 가사 및 돌봄
  CARE_WORKER("요양 보호사"),
  CAREGIVER("간병인"),
  FLYER_DISTRIBUTOR("전단지 배포"),
  BABYSITTER("베이비시터"),

  // 일용직
  CONSTRUCTION_ASSISTANT("공사장 보조"),
  PACKAGING_INSPECTOR("포장 검수"),
  TASTING_GUIDE_DAY("시식 안내"),
  FARM_WORK("농촌 일손");

  @JsonValue
  private final String value;

  // json형식으로 변환
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
