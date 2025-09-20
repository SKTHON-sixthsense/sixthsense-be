package com.skthon.sixthsensebe.global.config.converter;

import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.DetailJobCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringToDetailJobCategoryListConverter implements Converter<String[], List<DetailJobCategory>> {

  @Override
  public List<DetailJobCategory> convert(String[] source) {
    return Arrays.stream(source)
        .map(DetailJobCategory::fromValue)
        .collect(Collectors.toList());
  }
}