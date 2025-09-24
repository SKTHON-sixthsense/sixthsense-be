package com.skthon.sixthsensebe.domain.healtharray.controller;

import com.skthon.sixthsensebe.domain.healtharray.service.GptService;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.exception.UserErrorCode;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class healthArrayController {

  private final GptService gptService;
  private final UserRepository userRepository;

  @GetMapping("/recommend/{userId}")
  public ResponseEntity<Integer> recommendJob(@PathVariable Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<Health> userHealthList = user.getHealthList();

    Integer recommendedJobId = gptService.gptFlow(userHealthList);

    return ResponseEntity.ok(recommendedJobId);
  }
}
