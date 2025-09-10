package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, String date) {
        if (carId == null || date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "carId and date must be provided");
        }

        LocalDate localDate = checkDateValid(date);
        if (carRepository.findById(carId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Car with id %d not found", carId));
        }

        return policyRepository.existsActiveOnDate(carId, localDate);
    }

    public Car getCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car with ID " + carId + " not found"));
    }

    private LocalDate checkDateValid(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Invalid date format: '%s'. Expected format: yyyy-MM-dd", date));
        }

        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (year < 1950 || year > Year.now().getValue() + 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Year %d is outside supported range", year));
        }

        if (month < 1 || month > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Month %d is invalid.", month));
        }

        if (day < 1 || day > 31) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Day %d is invalid", day));
        }

        int maxDay = Month.of(month).length(Year.isLeap(year));
        if (day > maxDay) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Day %d doesn't exist for %d-%d. Must be between 01 and %d.", day, year, month, maxDay));
        }

        return LocalDate.of(year, month, day);
    }
}
