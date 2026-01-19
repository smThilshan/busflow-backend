package com.busflow.management.entity;

import com.busflow.management.enums.IncomeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Income extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @NotNull
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)  // Store as STRING in DB (recommended)
    private IncomeType incomeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    // ================= TRIP INFO with prefixed columns =================
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numberOfTrips", column = @Column(name = "trip_number_of_trips")),
            @AttributeOverride(name = "fromAmount", column = @Column(name = "trip_from_amount")),
            @AttributeOverride(name = "date", column = @Column(name = "trip_date")),
            @AttributeOverride(name = "toAmount", column = @Column(name = "trip_to_amount")),
            @AttributeOverride(name = "otherExpense", column = @Column(name = "trip_other_expense")),
            @AttributeOverride(name = "driverSalary", column = @Column(name = "trip_driver_salary")),
            @AttributeOverride(name = "conductorSalary", column = @Column(name = "trip_conductor_salary"))
    })
    private TripInfo tripInfo;

    // ================= HIRE INFO with prefixed columns =================
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numberOfDays", column = @Column(name = "hire_number_of_days")),
            @AttributeOverride(name = "date", column = @Column(name = "hire_date")),
            @AttributeOverride(name = "fromLocation", column = @Column(name = "hire_from_location")),
            @AttributeOverride(name = "destination", column = @Column(name = "hire_destination")),
            @AttributeOverride(name = "otherExpense", column = @Column(name = "hire_other_expense")),
            @AttributeOverride(name = "driverSalary", column = @Column(name = "hire_driver_salary")),
            @AttributeOverride(name = "conductorSalary", column = @Column(name = "hire_conductor_salary"))
    })
    private HireInfo hireInfo;
}
