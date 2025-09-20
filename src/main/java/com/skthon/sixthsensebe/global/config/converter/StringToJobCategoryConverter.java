package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.jobposting.entity.JobCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToJobCategoryConverter implements Converter<String, JobCategory> {

  @Override
  public JobCategory convert(String source) {
    return JobCategory.fromValue(source);
  }
}