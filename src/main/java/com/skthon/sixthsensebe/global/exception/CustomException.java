package com.skthon.sixthsensebe.global.exception.model;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
  private final BaseErrorCode errorCode;

  public CustomException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
