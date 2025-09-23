package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.jobposting.entity.SalaryType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSalaryConverter implements Converter<String, SalaryType> {
  @Override
  public SalaryType convert(String source) {
    return SalaryType.fromValue(source);
  }
}
