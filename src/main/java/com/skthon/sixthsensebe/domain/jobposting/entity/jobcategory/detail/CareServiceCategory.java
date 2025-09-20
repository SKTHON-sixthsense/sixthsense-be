package com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CareServiceCategory {

  // 가사 및 돌봄
  CARE_WORKER("요양 보호사"),
  CAREGIVER("간병인"),
  FLYER_DISTRIBUTOR("전단지 배포"),
  BABYSITTER("베이비시터");

  private final String value;

}
