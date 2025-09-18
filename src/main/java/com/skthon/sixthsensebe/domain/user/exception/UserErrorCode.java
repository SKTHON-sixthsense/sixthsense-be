package com.skthon.sixthsensebe.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.skthon.sixthsensebe.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  EXAMPLE_ERROR_CODE("USER_0000", "예시 에러코드로 커스터마이징이 필요합니다.", HttpStatus.BAD_REQUEST),

  DUPLICATE_USERNAME("USER_0001", "이미 사용 중인 아이디입니다.", HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD("USER_0002", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  IMAGE_UPLOAD_FAILED("USER_0003", "프로필 이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_SAVE_FAILED("USER_0004", "회원 정보 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_NOT_FOUND("USER_0005", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  USER_DELETE_FAILED("USER_0006", "사용자 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  S3_DELETE_FAILED("USER_0007", "S3 이미지 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
