package com.skthon.sixthsensebe.domain.jobposting.exception;

import com.skthon.sixthsensebe.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobPostingErrorCode implements BaseErrorCode {

  // 404 Not Found
  JOB_POSTING_NOT_FOUND("JP001", "채용공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // 409 Conflict
  DUPLICATE_JOB_POSTING("JP011", "동일한 채용공고가 이미 존재합니다.", HttpStatus.CONFLICT),
  JOB_POSTING_ALREADY_CLOSED("JP012", "이미 마감된 채용공고입니다.", HttpStatus.CONFLICT),
  JOB_POSTING_ALREADY_PUBLISHED("JP013", "이미 게시된 채용공고입니다.", HttpStatus.CONFLICT),

  // 500 Internal Server Error
  JOB_POSTING_CREATE_FAILED("JP014", "채용공고 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  JOB_POSTING_DELETE_FAILED("JP016", "채용공고 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
