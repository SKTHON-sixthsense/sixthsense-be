package com.skthon.sixthsensebe.global.naverocr.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageFormat {
  JPG("jpg"),
  JPEG("jpeg"),
  PNG("png"),
  PDF("pdf"),
  TIFF("tiff"),
  TIF("tif");

  @JsonValue
  private final String value;

  // 파일 확장자로부터 ImageFormat을 찾는 메서드
  public static ImageFormat fromExtension(String extension) {
    if (extension == null || extension.isEmpty()) {
      throw new IllegalArgumentException("확장자를 찾을 수 없습니다.");
    }

    String cleanExtension = extension.toLowerCase().startsWith(".")
        ? extension.substring(1).toLowerCase()
        : extension.toLowerCase();

    for (ImageFormat format : ImageFormat.values()) {
      if (format.getValue().equals(cleanExtension)) {
        return format;
      }
    }

    throw new IllegalArgumentException("지원하지 않는 확장자 파일입니다. : " + extension);
  }


  // 파일명으로부터 ImageFormat을 추출하는 메서드
  public static ImageFormat fromFileName(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("파일명에서 확장자를 찾을 수 없습니다.");
    }

    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      throw new IllegalArgumentException("파일명에 확장자가 포합되지 않았습니다. : " + fileName);
    }

    String extension = fileName.substring(lastDotIndex + 1);
    return fromExtension(extension);
  }
}

}
