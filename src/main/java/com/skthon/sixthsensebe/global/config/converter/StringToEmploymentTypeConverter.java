package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.jobposting.entity.EmploymentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEmploymentTypeConverter implements Converter<String, EmploymentType> {

  @Override
  public EmploymentType convert(String source) {
    return EmploymentType.fromValue(source);
  }
}