package com.skthon.sixthsensebe.global.exception.model;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
  String getCode();

  String getMessage();

  HttpStatus getStatus();
}
