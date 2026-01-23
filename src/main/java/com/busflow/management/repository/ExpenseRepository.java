package com.busflow.management.repository;

import com.busflow.management.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.bus.id = :busId
    AND DATE(e.createdAt) = CURRENT_DATE
    
""")

    List<Expense> findByBusId(Long busId);

    List<Expense> findByBusIdIn(List<Long> busIds);

    List<Expense> findByBusIdInAndTransactionDateBetween(List<Long> busIds, LocalDate startDate, LocalDate endDate);

    List<Expense> findByBusIdAndTransactionDateBetween(Long busId, LocalDate startDate, LocalDate endDate);

    List<Expense> findByBusIdInAndCategory(List<Long> accessibleBusIds, String category);
}
