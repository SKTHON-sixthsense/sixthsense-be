package com.skthon.sixthsensebe.global.naverocr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skthon.sixthsensebe.global.naverocr.entity.ImageFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageRequest {

  @JsonProperty("format")
  private ImageFormat format;

  @JsonProperty("name")
  private String name; // 파일명

  @JsonProperty("url")
  // 이미지 url (s3에 업로드된 pdf파일의 객체 url로 전송 -> 보안정책기에서 public으로 변경하여 누구나 접근 가능)
  private String url; // 이미지 s3 url

}
