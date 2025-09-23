package com.skthon.sixthsensebe.domain.jobposting.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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

  @JsonCreator
  public static SalaryType fromValue(String value) {
    for (SalaryType type : values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown employment type: " + value);
  }

}
