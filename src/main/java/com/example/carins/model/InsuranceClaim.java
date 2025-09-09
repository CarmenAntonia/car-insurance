package com.example.carins.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "insuranceclaim")
@Setter
@Getter
@NoArgsConstructor
public class InsuranceClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Car car;

    @NotNull
    private LocalDate claimDate;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal amount;

    public InsuranceClaim(Car car, LocalDate claimDate, String description, BigDecimal amount) {
        this.car = car;
        this.claimDate = claimDate;
        this.description = description;
        this.amount = amount;
    }
}
