package com.busflow.management.repository;

import com.busflow.management.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.bus.id = :busId
    AND DATE(e.createdAt) = CURRENT_DATE
    
""")
    Double getTodayExpenseByBus(Long busId);

}
