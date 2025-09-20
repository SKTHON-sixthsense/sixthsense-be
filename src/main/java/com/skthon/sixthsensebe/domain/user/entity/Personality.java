package com.skthon.sixthsensebe.domain.user.entity;

import com.skthon.sixthsensebe.domain.user.dto.response.TagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public enum Personality {

  @Schema(description = "성실하다")
  DILIGENT("DILIGENT", "성실하다"),

  @Schema(description = "손이 빨라요")
  FAST("FAST", "손이 빨라요"),

  @Schema(description = "시간을 잘지켜요")
  PUNCTUAL("PUNCTUAL", "시간을 잘지켜요"),

  @Schema(description = "책임감이 있어요")
  RESPONSIBLE("RESPONSIBLE", "책임감이 있어요"),

  @Schema(description = "꼼꼼해요")
  METICULOUS("METICULOUS", "꼼꼼해요"),

  @Schema(description = "사교적이다")
  SOCIABLE("SOCIABLE", "사교적이다"),

  @Schema(description = "계획적이에요")
  PLANNED("PLANNED", "계획적이에요"),

  @Schema(description = "힘이 좋아요")
  STRONG("STRONG", "힘이 좋아요"),

  @Schema(description = "잘 웃어요")
  SMILEY("SMILEY", "잘 웃어요"),

  @Schema(description = "순발력이 좋아요")
  QUICK("QUICK", "순발력이 좋아요"),

  @Schema(description = "긍정적이에요")
  POSITIVE("POSITIVE", "긍정적이에요"),

  @Schema(description = "말을 잘해요")
  TALKATIVE("TALKATIVE", "말을 잘해요"),

  @Schema(description = "열정적이에요")
  PASSIONATE("PASSIONATE", "열정적이에요"),

  @Schema(description = "융통성이 있어요")
  FLEXIBLE("FLEXIBLE", "융통성이 있어요");

  private final String code;
  private final String koName;

  Personality(String code, String koName) {
    this.code = code;
    this.koName = koName;
  }

  /**
   * 요청 바인딩 시 한글/영문 둘 다 허용
   */
  public static Personality from(String value) {
    if (value == null) {
      return null;
    }
    String v = value.trim();
    for (Personality p : values()) {
      if (p.code.equalsIgnoreCase(v) || p.koName.equals(v)) {
        return p;
      }
    }
    throw new IllegalArgumentException("Invalid personality value: " + value);
  }

  /**
   * 응답 변환용 TagDto(code, name)
   */
  public TagDto toTagDto() {
    return TagDto.of(this.code, this.koName);
  }
}
