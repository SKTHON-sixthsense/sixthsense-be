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

  private static final String ACCESS_TOKEN_COOKIE = "accessToken";
  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  private static final String REDIS_BLACKLIST_PREFIX = "blacklist:";
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

  // WORKER 테스트용 로그인 계정(ID = 1)을 통해 로그인을 처리
  public UserResponse testLoginWorker(HttpServletResponse response) {
    User user = userRepository.findById(1L)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return issueTokensAndSetResponse(user, response);
  }

  // OWNER 테스트용 로그인 계정(ID = 2)을 통해 로그인을 처리
  public UserResponse testLoginOwner(HttpServletResponse response) {
    User user = userRepository.findById(2L)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return issueTokensAndSetResponse(user, response);
  }


  /**
   * 로그아웃 처리 메서드
   *
   * <p>쿠키에 저장된 액세스 토큰을 검증하여 Redis 블랙리스트에 등록하고,
   * 사용자 리프레시 토큰을 Redis에서 삭제한 뒤 브라우저의 액세스/리프레시 쿠키를 즉시 만료시킨다.
   *
   * <ul>
   *   <li>Access Token: 쿠키에서 추출 → 유효성 검증 → 블랙리스트 등록(만료 시각까지)</li>
   *   <li>Refresh Token: Redis 저장값 삭제 → 브라우저 쿠키 삭제</li>
   *   <li>쿠키 정리: accessToken / refreshToken 둘 다 Max-Age=0 으로 무효화</li>
   * </ul>
   *
   * @param response HTTP 응답 객체 (accessToken/refreshToken 쿠키 만료 설정용)
   * @throws CustomException 액세스 토큰이 없거나 유효하지 않은 경우 {@link AuthErrorCode#INVALID_ACCESS_TOKEN}
   */
  private void deleteAccessTokenCookie(HttpServletResponse response) {
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from("accessToken", "")
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

  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = extractCookie(request, ACCESS_TOKEN_COOKIE); // 상수 사용
    if (accessToken == null || !jwtProvider.validateToken(accessToken)) {
      throw new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    long expiration =
        jwtProvider.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
    redisUtil.setData(REDIS_BLACKLIST_PREFIX + accessToken, "logout", expiration / 1000);

    Long userId = jwtProvider.extractUserId(accessToken);
    redisUtil.deleteData(REFRESH_TOKEN_PREFIX + userId);

    deleteRefreshTokenCookie(response);
    deleteAccessTokenCookie(response);
  }

  private String extractCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) {
      return null;
    }
    for (Cookie cookie : request.getCookies()) {
      if (name.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  /**
   * 액세스 토큰 재발급 - 쿠키의 refreshToken 검증 → 새 accessToken 발급
   */
  public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshTokenFromCookie(request);
    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    Long userId = jwtProvider.extractUserId(refreshToken);
    String storedToken = redisUtil.getData(REFRESH_TOKEN_PREFIX + userId);
    if (!refreshToken.equals(storedToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    // 새 accessToken을 쿠키로 내려줌 (헤더 X)
    String newAccessToken = jwtProvider.createAccessToken(userId);
    setAccessTokenCookie(response, newAccessToken, jwtProvider.getAccessTokenExpireTime() / 1000);
  }

  // 사용자 인증
  private User validateUserCredentials(UserRequest.LoginRequest loginRequest) {
    User user =
        userRepository
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

    setAccessTokenCookie(response, accessToken, jwtProvider.getAccessTokenExpireTime() / 1000);
    setRefreshTokenCookie(response, refreshToken, refreshTokenExpireSeconds);

    return userMapper.toResponse(user);
  }

  private void setAccessTokenCookie(HttpServletResponse response, String accessToken,
      long maxAgeSec) {
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
            .httpOnly(true)       // XSS에 안전
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSec));

    if (secure) {
      cookie.secure(true).sameSite("None"); // 크로스 도메인일 때
    } else {
      cookie.secure(false).sameSite("Lax"); // 로컬 개발
    }
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
  }

  // 토큰 쿠키 설정
  private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken,
      long maxAgeSec) {
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSec));

    if (secure) {
      cookie.secure(true).sameSite("None");
    } else {
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
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
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
