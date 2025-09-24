package com.skthon.sixthsensebe.domain.favorite.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FavoriteType {
  JOB_POSTING("JOB_POSTING"),
  EDUCATION("EDUCATION");

  @JsonValue
  private final String value;

  @JsonCreator
  public static FavoriteType fromValue(String value) {
    for (FavoriteType type : FavoriteType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown FavoriteType: " + value);
  }
}