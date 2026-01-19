package com.busflow.management.repository;

import com.busflow.management.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {

    List<Bus> findByOwnerId(Long ownerId);
}
