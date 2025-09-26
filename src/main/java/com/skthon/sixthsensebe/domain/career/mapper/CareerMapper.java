package com.skthon.sixthsensebe.domain.career.mapper;

import com.skthon.sixthsensebe.domain.career.dto.request.CareerRequest;
import com.skthon.sixthsensebe.domain.career.dto.response.CareerResponse;
import com.skthon.sixthsensebe.domain.career.entity.Career;
import com.skthon.sixthsensebe.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CareerMapper {

  public Career toEntity(CareerRequest.Create req, User user) {
    return Career.builder()
        .user(user)
        .companyName(req.getCompanyName())
        .startDate(req.getStartDate())
        .endDate(req.getEndDate())
        .current(req.getEndDate() == null)
        .task(req.getTask())
        .build();
  }

  public CareerResponse toResponse(Career c) {
    return CareerResponse.builder()
        .id(c.getId())
        .companyName(c.getCompanyName())
        .startDate(c.getStartDate())
        .endDate(c.getEndDate())
        .current(c.isCurrent())
        .createdAt(c.getCreatedAt())
        .task(c.getTask())
        .build();
  }
}
