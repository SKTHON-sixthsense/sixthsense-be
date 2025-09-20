package com.skthon.sixthsensebe.domain.introduction.repository;

import com.skthon.sixthsensebe.domain.introduction.entity.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {
}
