package com.example.carinsurance.domain.repository;

import com.example.carinsurance.domain.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
}
