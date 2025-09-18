package com.skthon.sixthsensebe.domain.auth.service;


import com.skthon.sixthsensebe.domain.auth.exception.AuthErrorCode;
import com.skthon.sixthsensebe.domain.user.dto.request.UserRequest;
import com.skthon.sixthsensebe.domain.user.dto.response.UserResponse;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.mapper.UserMapper;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.domain.user.service.UserService;
import com.skthon.sixthsensebe.global.exception.CustomException;
import com.skthon.sixthsensebe.global.jwt.JwtProvider;
import com.skthon.sixthsensebe.global.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RedisUtil redisUtil;
  private final UserMapper userMapper;
  private final UserService userService;
  private static final String REFRESH_TOKEN_PREFIX = "user:refresh:";

  @Value("${cookie.secure}")
  private boolean secure;

  /**
   * 일반 로그인을 처리하는 메서드
   *
   * @param loginRequest 사용자 로그인 요청 객체 (이메일, 비밀번호 포함)
   * @param response     액세스 토큰과 리프레시 토큰을 담기 위한 HTTP 응답 객체
   * @return 로그인한 사용자 정보가 담긴 {@link UserResponse} 객체
   * @throws CustomException 이메일에 해당하는 사용자가 없을 경우 {@link AuthErrorCode#INVALID_PASSWORD}
   * @throws CustomException 비밀번호가 일치하지 않을 경우 {@link AuthErrorCode#INVALID_PASSWORD}
   */
  public UserResponse login(UserRequest.LoginRequest loginRequest, HttpServletResponse response) {
    User user = validateUserCredentials(loginRequest);
    return issueTokensAndSetResponse(user, response);
  }

  /**
   * 테스트용 로그인 계정(ID = 1)을 통해 로그인을 처리하는 메서드
   *
   * @param response 액세스 토큰과 리프레시 토큰을 담기 위한 HTTP 응답 객체
   * @return 로그인한 테스트 사용자 정보가 담긴 {@link UserResponse} 객체
   * @throws CustomException ID가 1인 테스트 사용자가 존재하지 않을 경우
   *                         {@link AuthErrorCode#AUTHENTICATION_NOT_FOUND}
   */
  public UserResponse testLogin(HttpServletResponse response) {
    User user =
        userRepository
            .findById(1L)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return issueTokensAndSetResponse(user, response);
  }

  /**
   * 로그아웃 처리 메서드
   *
   * <p>요청 헤더에서 액세스 토큰을 추출하여 Redis 블랙리스트에 저장하고, 리프레시 토큰을 Redis에서 삭제하여 재사용을 차단합니다.
   *
   * @param request  HTTP 요청 객체 (헤더에서 Access Token 추출용)
   * @param response HTTP 응답 객체 (리프레시 쿠키 삭제용)
   * @throws CustomException 액세스 토큰이 유효하지 않거나 없을 경우 {@link AuthErrorCode#INVALID_ACCESS_TOKEN}
   */
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = resolveAccessToken(request);
    if (accessToken == null || !jwtProvider.validateToken(accessToken)) {
      throw new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    // 블랙리스트 등록 (accessToken → "logout" 값, 만료시간까지)
    long expiration =
        jwtProvider.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
    redisUtil.setData("blacklist:" + accessToken, "logout", expiration / 1000);

    // refresh 토큰 Redis에서 삭제
    Long userId = jwtProvider.extractUserId(accessToken);
    redisUtil.deleteData("user:refresh:" + userId);

    // 쿠키에서 refreshToken 제거
    deleteRefreshTokenCookie(response);
  }

  /**
   * 액세스 토큰 재발급 처리 메서드
   *
   * <p>쿠키에서 리프레시 토큰을 추출한 후 Redis에 저장된 토큰과 비교하여 유효성을 검증합니다. 검증에 성공하면 새로운 액세스 토큰을 생성하여 응답 헤더에
   * 포함시킵니다.
   *
   * @param request  HTTP 요청 객체 (쿠키에서 리프레시 토큰 추출용)
   * @param response HTTP 응답 객체 (새로운 액세스 토큰 설정용)
   * @throws CustomException 리프레시 토큰이 없거나 유효하지 않거나, 저장된 토큰과 일치하지 않는 경우
   *                         {@link AuthErrorCode#REFRESH_TOKEN_REQUIRED}
   */
  public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
    // 1. 쿠키에서 refreshToken 추출
    String refreshToken = extractRefreshTokenFromCookie(request);
    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    // 2. 사용자 ID 추출
    Long userId = jwtProvider.extractUserId(refreshToken);

    // 3. Redis에 저장된 리프레시 토큰과 비교
    String storedToken = redisUtil.getData("user:refresh:" + userId);
    if (!refreshToken.equals(storedToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    // 4. 새로운 accessToken 생성 후 응답 헤더에 설정
    String newAccessToken = jwtProvider.createAccessToken(userId);
    response.setHeader("Authorization", "Bearer " + newAccessToken);
  }

  // 사용자 인증
  private User validateUserCredentials(UserRequest.LoginRequest loginRequest) {
    User user = userRepository
        .findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_PASSWORD)); // 아이디 틀림

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new CustomException(AuthErrorCode.INVALID_PASSWORD); // 비밀번호 틀림
    }
    return user;
  }


  // 토큰 발급 및 응답 세팅
  private UserResponse issueTokensAndSetResponse(User user, HttpServletResponse response) {
    String accessToken = jwtProvider.createAccessToken(user.getId());
    String refreshToken = jwtProvider.createRefreshToken(user.getId());

    long refreshTokenExpireSeconds = jwtProvider.getRefreshTokenExpireTime() / 1000;
    redisUtil.setData(REFRESH_TOKEN_PREFIX + user.getId(), refreshToken, refreshTokenExpireSeconds);

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenCookie(response, refreshToken, refreshTokenExpireSeconds);

    return userMapper.toResponse(user);
  }

  private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader("Authorization", "Bearer " + accessToken);
  }

  // 토큰 쿠키 설정
  private void setRefreshTokenCookie(
      HttpServletResponse response, String refreshToken, long maxAgeSec) {

    ResponseCookie.ResponseCookieBuilder cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofSeconds(maxAgeSec));

    if (secure) {
      cookie.secure(true).sameSite("None");
    } else {
      // 로컬 개발환경
      cookie.secure(false).sameSite("Lax");
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    for (Cookie cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private String resolveAccessToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }

  private void deleteRefreshTokenCookie(HttpServletResponse response) {
    ResponseCookie.ResponseCookieBuilder cookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ZERO);

    if (secure) {
      cookie.secure(true).sameSite("None");
    } else {
      cookie.secure(false).sameSite("Lax");
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
  }

  /**
   * 현재 세션(토큰)을 무효화합니다. - AccessToken: 블랙리스트 등록 - RefreshToken: Redis 삭제 - 쿠키: refreshToken 즉시 만료
   *
   * <p>logout()을 호출하되 예외가 나도 흡수해서 탈퇴 트랜잭션에 영향 주지 않음.
   */
  public void invalidateCurrentSessionQuietly(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      logout(request, response);
    } catch (CustomException ignore) {
      deleteRefreshTokenCookie(response);
    }
  }
}

