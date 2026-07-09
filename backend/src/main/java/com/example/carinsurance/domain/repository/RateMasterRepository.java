package com.example.carinsurance.domain.repository;

import com.example.carinsurance.domain.entity.RateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RateMasterRepository extends JpaRepository<RateMaster, Long> {
    List<RateMaster> findByActiveTrue();
}
