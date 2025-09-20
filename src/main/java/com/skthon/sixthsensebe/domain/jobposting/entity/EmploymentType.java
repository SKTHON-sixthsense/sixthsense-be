package com.skthon.sixthsensebe.domain.jobposting.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentType {
  PARTTIME("아르바이트"),
  REGULAR("정규직");

  @JsonValue
  private final String value;

  @JsonCreator
  public static EmploymentType fromValue(String value) {
    for (EmploymentType type : values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown employment type: " + value);
  }
}
