package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.DetailJobCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDetailJobCategoryConverter implements Converter<String, DetailJobCategory> {

  @Override
  public DetailJobCategory convert(String source) {
    return DetailJobCategory.fromValue(source);
  }
}