package com.skthon.sixthsensebe.domain.jobapplication.repository;

import com.skthon.sixthsensebe.domain.jobapplication.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

}
