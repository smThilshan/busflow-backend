package com.busflow.management.repository;

import com.busflow.management.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncomeRepository extends JpaRepository<Income, Long> {
//
//    @Query("""
//        SELECT COALESCE(SUM(i.amount), 0)
//        FROM Income i
//        WHERE i.bus.id = :busId
//        AND i.createdAt >= CURRENT_DATE
//        AND i.createdAt < CURRENT_DATE + 7
//    """)


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


//    Double getWeeklyIncomeByBus(@Param("busId") Long busId);

}
