package com.example.carins.service;

import com.example.carins.mapper.InsurancePolicyMapper;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.InsurancePolicyDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class InsurancePolicyService {

    @Autowired
    private final InsurancePolicyRepository insurancePolicyRepository;

    @Autowired
    private final InsurancePolicyMapper insurancePolicyMapper;

    public InsurancePolicy addInsurance(InsurancePolicyDto insurancePolicyDto) {
        validateDto(insurancePolicyDto);
        return insurancePolicyRepository.save(insurancePolicyMapper.toEntity(insurancePolicyDto));
    }

    public InsurancePolicy updateInsurance(Long id, InsurancePolicyDto insurancePolicyDto) {
        validateDto(insurancePolicyDto);

        InsurancePolicy existing = insurancePolicyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Insurance policy with ID " + id + " not found"));

        insurancePolicyMapper.updateEntity(existing, insurancePolicyDto);
        return insurancePolicyRepository.save(existing);
    }

    private void validateDto(InsurancePolicyDto dto) {
        if (dto.provider() == null || dto.provider().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider name must be set");
        }
        if (dto.startDate() == null || dto.endDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates must be provided");
        }
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
    }
}
