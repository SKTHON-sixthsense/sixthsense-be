package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DetailJobCategory {

  // 시설관리 및 운영
  SECURITY_SAFETY("경비 및 보안", JobCategory.FACILITY_MANAGEMENT),
  PARKING_MANAGEMENT("주차관리 및 안내", JobCategory.FACILITY_MANAGEMENT),
  CLEANING_BEAUTY("청소 및 미화", JobCategory.FACILITY_MANAGEMENT),
  STORE_MANAGEMENT("매장 관리", JobCategory.FACILITY_MANAGEMENT),

  // 요식업
  DISHWASHER("설거지", JobCategory.FOOD_SERVICE),
  KITCHEN_ASSISTANT("주방 보조", JobCategory.FOOD_SERVICE),
  HALL_SERVICE("홀 서빙", JobCategory.FOOD_SERVICE),
  COFFEE_PREPARATION("커피 제조", JobCategory.FOOD_SERVICE),

  // 운전 및 배송
  PARCEL_LOADING("택배 상하차", JobCategory.DELIVERY_TRANSPORT),
  FREIGHT_DRIVER("남품 운전", JobCategory.DELIVERY_TRANSPORT),
  DELIVERY_DRIVER("배송 운전", JobCategory.DELIVERY_TRANSPORT),
  SUBSTITUTE_DRIVER("대리 운전", JobCategory.DELIVERY_TRANSPORT),

  // 판매직
  CASHIER("계산", JobCategory.SALES),
  PRODUCT_DISPLAY("상품 진열", JobCategory.SALES),
  TASTING_GUIDE("시식 안내", JobCategory.SALES),
  SALES_ASSISTANT("판매 보조", JobCategory.SALES),

  // 가사 및 돌봄
  CARE_WORKER("요양보호사", JobCategory.CARE_SERVICE),
  CAREGIVER("간병인", JobCategory.CARE_SERVICE),
  FLYER_DISTRIBUTOR("전단지 배포", JobCategory.CARE_SERVICE),
  BABYSITTER("베이비시터", JobCategory.CARE_SERVICE),

  // 일용직
  CONSTRUCTION_ASSISTANT("공사장 보조", JobCategory.DAY_LABOR),
  PACKAGING_INSPECTOR("포장검수", JobCategory.DAY_LABOR),
  TASTING_GUIDE_DAY("시식 안내", JobCategory.DAY_LABOR),
  FARM_WORK("농촌 일손", JobCategory.DAY_LABOR);

  @JsonValue
  private final String value;
  private final JobCategory parentCategory;

  @JsonCreator
  public static DetailJobCategory fromValue(String value) {
    for (DetailJobCategory category : values()) {
      if (category.getValue().equals(value)) {
        return category;
      }
    }
    throw new IllegalArgumentException("Unknown detail job category: " + value);
  }

  public boolean belongsTo(JobCategory parentCategory) {
    return this.parentCategory == parentCategory;
  }
}