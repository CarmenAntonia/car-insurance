package com.example.carins.service;

import com.example.carins.mapper.InsurancePolicyMapper;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.InsurancePolicyDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
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

        InsurancePolicy existing = insurancePolicyRepository.findInsurancePolicyWithCarId(id, insurancePolicyDto.carId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Insurance policy with ID " + id + " for car with ID " + insurancePolicyDto.carId() + " not found"));

        insurancePolicyMapper.updateEntity(existing, insurancePolicyDto);
        return insurancePolicyRepository.save(existing);
    }

    private void validateDto(InsurancePolicyDto dto) {
        if(dto.carId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car ID is required");
        }
        if (dto.startDate() == null || dto.endDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates must be provided");
        }
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }
    }

    @Scheduled(cron = "0 * 0 * * *")
    public void logExpiredPolicies() {
        LocalDate today = LocalDate.now();
        List<InsurancePolicy> expiredPolicies = insurancePolicyRepository.findExpired(today.minusDays(1));

        LocalDateTime now = LocalDateTime.now();
        List<InsurancePolicy> loggedPolicies = expiredPolicies.stream()
                .filter(policy -> {
                    LocalDateTime expiryTime = policy.getEndDate().plusDays(1).atStartOfDay();
                    return now.isBefore(expiryTime.plusHours(1));
                })
                .peek(policy -> {
                    log.info("Policy {} for car {} expired on {}",
                            policy.getId(),
                            policy.getCar().getId(),
                            policy.getEndDate());
                    policy.setExpiredLogged(true);
                })
                .toList();

        if (!loggedPolicies.isEmpty()) {
            insurancePolicyRepository.saveAll(expiredPolicies);
        }
    }
}
