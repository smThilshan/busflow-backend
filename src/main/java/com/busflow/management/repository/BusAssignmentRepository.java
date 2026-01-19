package com.busflow.management.repository;

import com.busflow.management.entity.Bus;
import com.busflow.management.entity.BusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusAssignmentRepository  extends JpaRepository<BusAssignment, Long> {

//    check if user has active access  to bus
    boolean existsByUserIdAndBusIdAndIsActiveTrue(Long userId, Long busId);

//    get all active buses for a conductor
    List<BusAssignment> findByUserIdAndIsActiveTrue(Long userId);

//    get all conductors fot a bus
    List<BusAssignment> findByBusIdAndIsActiveTrue(Long busId);
}
