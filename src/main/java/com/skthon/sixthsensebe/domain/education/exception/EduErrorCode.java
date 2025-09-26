package com.skthon.sixthsensebe.domain.education.exception;

import com.skthon.sixthsensebe.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EduErrorCode implements BaseErrorCode {

  EDU_NOT_FOUND("POST_4001", "해당 게시글을 찾을 수 없습니다", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;

}
