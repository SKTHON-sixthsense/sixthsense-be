package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.search.entity.Seoul;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSeoulConverter implements Converter<String, Seoul> {

  @Override
  public Seoul convert(String source) {
    return Seoul.fromValue(source);
  }
}