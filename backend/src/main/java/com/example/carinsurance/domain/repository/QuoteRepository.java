package com.example.carinsurance.domain.repository;

import com.example.carinsurance.domain.entity.Quote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    @EntityGraph(attributePaths = "breakdowns")
    Optional<Quote> findByQuoteNo(String quoteNo);
    
    boolean existsByQuoteNo(String quoteNo);
}
