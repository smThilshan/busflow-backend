package com.busflow.management.repository;

import com.busflow.management.entity.Bus;
import com.busflow.management.entity.BusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusAssignmentRepository  extends JpaRepository<BusAssignment, Long> {

//    check if user has active access  to bus
    boolean existsByUserIdAndBusIdAndIsActiveTrue(Long userId, Long busId);

//    get all conductors for the bus
    List<BusAssignment> findByUserIdAndIsActiveTrue(Long userId);

//    get all active buses for a conductor
    List<BusAssignment> findByBusIdAndIsActiveTrue(Long busId);

    // Get all assignments for a user (active and inactive)
    List<BusAssignment> findByUserId(Long userId);

    // Get all assignments for a bus (active and inactive)
    List<BusAssignment> findByBusId(Long busId);

    // Get all assignments for multiple buses
    List<BusAssignment> findByBusIdIn(List<Long> busIds);
}
