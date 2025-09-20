package com.skthon.sixthsensebe.domain.healtharray.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GptRequest {
  private Long id;
  private String jobCategory;
  private List<String> detailJobCategories;
}
