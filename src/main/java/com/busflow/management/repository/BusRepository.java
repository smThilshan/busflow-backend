package com.busflow.management.repository;

import com.busflow.management.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {

    List<Bus> findByOwnerId(Long ownerId);

    boolean existsByBusNumber(String busNumber);

    Optional<Bus> findByBusNumber(String busNumber);
}
