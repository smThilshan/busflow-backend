package com.busflow.management.service;

import com.busflow.management.dto.AssignBusRequestDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.repository.BusRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusAssignmentService {

    private final UserRepository userRepository;
    private final BusRepository busRepository;

    public void assignConductorToBus(AssignBusRequestDTO request) {

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (owner.getRole() != Role.OWNER) {
            throw new RuntimeException("Only owner can assign conductors");
        }

        User conductor = userRepository.findById(request.getConductorId())
                .orElseThrow(() -> new RuntimeException("Conductor not found"));

        if (conductor.getRole() != Role.CONDUCTOR) {
            throw new RuntimeException("User is not a conductor");
        }

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        conductor.setBus(bus);
        userRepository.save(conductor);
    }
}
