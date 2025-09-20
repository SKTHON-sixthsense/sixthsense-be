package com.skthon.sixthsensebe.domain.user.dto.response;

import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.Personality;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그(코드/한글명) 응답 DTO")
public record TagDto(
    @Schema(description = "영문 코드", example = "BACK") String code,
    @Schema(description = "한글명", example = "허리") String name
) {

  public static TagDto of(String code, String name) {
    return new TagDto(code, name);
  }

  public static TagDto of(Health h) {
    return new TagDto(h.getCode(), h.getKoName());
  }

  public static TagDto of(Personality p) {
    return new TagDto(p.getCode(), p.getKoName());
  }
}

