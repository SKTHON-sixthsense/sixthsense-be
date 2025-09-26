package com.skthon.sixthsensebe.domain.user.entity;

import com.skthon.sixthsensebe.domain.user.dto.response.TagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum Health {

  @Schema(description = "다리")
  LEG("LEG", "다리"),

  @Schema(description = "무릎")
  KNEE("KNEE", "무릎"),

  @Schema(description = "허리")
  BACK("BACK", "허리"),

  @Schema(description = "어깨")
  SHOULDER("SHOULDER", "어깨"),

  @Schema(description = "손목")
  WRIST("WRIST", "손목"),

  @Schema(description = "눈")
  EYE("EYE", "눈"),

  @Schema(description = "귀")
  EAR("EAR", "귀"),

  @Schema(description = "호흡기")
  RESPIRATORY("RESPIRATORY", "호흡기");

  private final String code;
  private final String koName;

  Health(String code, String koName) {
    this.code = code;
    this.koName = koName;
  }

  /**
   * 요청 바인딩용: 영문 코드/한글명 둘 다 허용
   */
  public static Health from(String value) {
    if (value == null) {
      return null;
    }
    String v = value.trim();
    for (Health h : values()) {
      if (h.code.equalsIgnoreCase(v) || h.koName.equals(v)) {
        return h;
      }
    }
    throw new IllegalArgumentException("Invalid health value: " + value);
  }

  /**
   * 응답 변환용: TagDto(code, koName)
   */
  public TagDto toTagDto() {
    return TagDto.of(this.code, this.koName);
  }
}
