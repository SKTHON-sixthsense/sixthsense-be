package com.skthon.sixthsensebe.domain.career.exception;

import com.skthon.sixthsensebe.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CareerErrorCode implements BaseErrorCode {

  CAREER_NOT_FOUND("CAREER_0001", "경력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_NOT_FOUND("CAREER_0002", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  NO_OWNERSHIP("CAREER_0003", "본인의 경력만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
