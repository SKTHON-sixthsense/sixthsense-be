package com.skthon.sixthsensebe.domain.career.service;

import com.skthon.sixthsensebe.domain.career.dto.request.CareerRequest;
import com.skthon.sixthsensebe.domain.career.entity.Career;
import com.skthon.sixthsensebe.domain.career.exception.CareerErrorCode;
import com.skthon.sixthsensebe.domain.career.mapper.CareerMapper;
import com.skthon.sixthsensebe.domain.career.repository.CareerRepository;
import com.skthon.sixthsensebe.domain.user.entity.User;
import com.skthon.sixthsensebe.domain.user.repository.UserRepository;
import com.skthon.sixthsensebe.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareerService {

  private final CareerRepository careerRepository;
  private final UserRepository userRepository;
  private final CareerMapper careerMapper;
  
  @Transactional(readOnly = true)
  public List<Career> getMyCareers(Long userId) {
    return careerRepository.findByUser_Id(userId);
  }

  @Transactional
  public Career addCareer(Long userId, CareerRequest.Create dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(CareerErrorCode.USER_NOT_FOUND));

    Career career = careerMapper.toEntity(dto, user);

    // endDate가 null이면 현재 재직중으로 설정
    if (career.getEndDate() == null) {
      career.setCurrent(true);
    }

    return careerRepository.save(career);
  }

  @Transactional
  public void deleteCareer(Long userId, Long careerId) {
    Career career = careerRepository.findById(careerId)
        .orElseThrow(() -> new CustomException(CareerErrorCode.CAREER_NOT_FOUND));

    if (!career.getUser().getId().equals(userId)) {
      throw new CustomException(CareerErrorCode.NO_OWNERSHIP);
    }

    careerRepository.delete(career);
  }
}
