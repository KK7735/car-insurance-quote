package com.example.carinsurance.domain.service;

import com.example.carinsurance.domain.dto.RateMasterDto;
import com.example.carinsurance.domain.entity.RateMaster;
import com.example.carinsurance.domain.repository.RateMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateMasterService {

    private final RateMasterRepository rateMasterRepository;

    public List<RateMasterDto> getActiveRates() {
        return rateMasterRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RateMasterDto convertToDto(RateMaster entity) {
        return RateMasterDto.builder()
                .category(entity.getCategory())
                .itemCode(entity.getItemCode())
                .itemName(entity.getItemName())
                .rate(entity.getRate())
                .amount(entity.getAmount())
                .build();
    }
}
