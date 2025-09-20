package com.skthon.sixthsensebe.global.config;

import com.skthon.sixthsensebe.global.config.converter.StringToDetailJobCategoryConverter;
import com.skthon.sixthsensebe.global.config.converter.StringToDetailJobCategoryListConverter;
import com.skthon.sixthsensebe.global.config.converter.StringToEmploymentTypeConverter;
import com.skthon.sixthsensebe.global.config.converter.StringToJobCategoryConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final StringToJobCategoryConverter stringToJobCategoryConverter;
  private final StringToEmploymentTypeConverter stringToEmploymentTypeConverter;
  private final StringToDetailJobCategoryConverter stringToDetailJobCategoryConverter;
  private final StringToDetailJobCategoryListConverter stringToDetailJobCategoryListConverter;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(stringToJobCategoryConverter);
    registry.addConverter(stringToEmploymentTypeConverter);
    registry.addConverter(stringToDetailJobCategoryConverter);
    registry.addConverter(stringToDetailJobCategoryListConverter);
  }
}