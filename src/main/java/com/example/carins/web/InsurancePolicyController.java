package com.example.carins.web;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.dto.InsurancePolicyDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class InsurancePolicyController {

    @Autowired
    private final InsurancePolicyService insurancePolicyService;

    @PostMapping("/insurance")
    public ResponseEntity<InsurancePolicy> createPolicy(@RequestBody InsurancePolicyDto insurancePolicyDto) {
        InsurancePolicy insurancePolicy = insurancePolicyService.addInsurance(insurancePolicyDto);
        return ResponseEntity.ok(insurancePolicy);
    }

    @PutMapping("/insurance/{id}")
    public ResponseEntity<InsurancePolicy> updatePolicy(@PathVariable Long id, @RequestBody InsurancePolicyDto dto) {
        InsurancePolicy policy = insurancePolicyService.updateInsurance(id, dto);
        return ResponseEntity.ok(policy);
    }
}
