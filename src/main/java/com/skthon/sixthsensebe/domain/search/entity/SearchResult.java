package com.skthon.sixthsensebe.domain.search.entity;

import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.DetailJobCategory;
import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.JobCategory;
import com.skthon.sixthsensebe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "search_result")
public class SearchResult extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "district")
  private Seoul district;

  @ElementCollection(targetClass = JobCategory.class)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "search_result_job_categories", joinColumns = @JoinColumn(name = "search_result_id"))
  @Column(name = "job_category")
  private List<JobCategory> jobCategories;

  @ElementCollection(targetClass = DetailJobCategory.class)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "search_result_detail_categories", joinColumns = @JoinColumn(name = "search_result_id"))
  @Column(name = "detail_job_category")
  private List<DetailJobCategory> detailJobCategories;

  @ElementCollection
  @CollectionTable(name = "search_result_job_posting_ids", joinColumns = @JoinColumn(name = "search_result_id"))
  @Column(name = "job_posting_id")
  private List<Long> jobPostingIds;

  @Column(name = "result_count")
  private Integer resultCount;
}