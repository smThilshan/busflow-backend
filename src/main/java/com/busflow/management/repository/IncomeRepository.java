package com.busflow.management.repository;

import com.busflow.management.entity.Income;
import com.busflow.management.enums.IncomeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {


    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Income i
    WHERE i.bus.id = :busId
    AND i.createdAt >= :start
    AND i.createdAt < :end
""")
    Double getIncomeBetween(
            @Param("busId") Long busId,
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end
    );


    List<Income> findByBusId(Long busId);

    List<Income> findByBusIdIn(List<Long> busIds);

    List<Income> findByBusIdInAndTransactionDateBetween(List<Long> busIds, LocalDate startDate, LocalDate endDate);

    List<Income> findByBusIdAndTransactionDateBetween(Long busId, LocalDate startDate, LocalDate endDate);

    List<Income> findByBusIdInAndIncomeType(List<Long> busIds, IncomeType incomeType);

    List<Income> findByBusIdAndIncomeType(Long busId, IncomeType incomeType);

}

