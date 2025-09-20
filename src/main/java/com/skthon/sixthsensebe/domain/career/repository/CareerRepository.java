package com.skthon.sixthsensebe.domain.career.repository;

import com.skthon.sixthsensebe.domain.career.entity.Career;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {

  List<Career> findByUser_Id(Long userId);
}
