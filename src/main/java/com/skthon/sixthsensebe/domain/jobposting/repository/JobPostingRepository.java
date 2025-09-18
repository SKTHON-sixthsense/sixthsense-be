package com.skthon.sixthsensebe.domain.jobposting.repository;

import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
}
