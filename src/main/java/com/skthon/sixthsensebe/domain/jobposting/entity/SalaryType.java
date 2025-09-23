package com.skthon.sixthsensebe.domain.jobposting.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SalaryType {
  WEEKLYPAY("시급"),
  MONTHLYPAY("월급");

  @JsonValue
  private final String value;
}
