package com.skthon.sixthsensebe.domain.health.service;

import com.skthon.sixthsensebe.domain.user.dto.response.TagDto;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

  public List<TagDto> getAll() {
    return Arrays.stream(Health.values())
        .map(Health::toTagDto)
        .collect(Collectors.toList());
  }
}
