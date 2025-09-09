package com.example.carins.mapper;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.InsurancePolicyDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InsurancePolicyMapper {

    @Autowired
    private final CarService carService;

    public InsurancePolicy toEntity(InsurancePolicyDto insurancePolicyDto) {
        Car car = carService.getCarById(insurancePolicyDto.carId());
        return new InsurancePolicy(car, insurancePolicyDto.provider(), insurancePolicyDto.startDate(), insurancePolicyDto.endDate());
    }

    public void updateEntity(InsurancePolicy existing, InsurancePolicyDto insurancePolicyDto) {
        Car car = carService.getCarById(insurancePolicyDto.carId());

        existing.setCar(car);
        existing.setProvider(insurancePolicyDto.provider());
        existing.setStartDate(insurancePolicyDto.startDate());
        existing.setEndDate(insurancePolicyDto.endDate());
    }
}
