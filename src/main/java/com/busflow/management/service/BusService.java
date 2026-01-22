package com.busflow.management.service;

import com.busflow.management.dto.BusRequestDTO;
import com.busflow.management.dto.BusResponseDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.BusAssignment;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.exception.ResourceAlreadyExistsException;
import com.busflow.management.exception.ResourceNotFoundException;
import com.busflow.management.exception.UnauthorizedException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusService {
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;

//    Create new bus
    @Transactional
    public BusResponseDTO createBus(BusRequestDTO request, User owner) {

        if (owner == null) {
            throw new UnauthorizedException("User not authenticated");
        }


        // Validate owner role
        if (owner.getRole() != Role.OWNER){
            throw new UnauthorizedException("Only owner can create buses");
        }

        // Check if bus number already exists
        if (busRepository.existsByBusNumber(request.getBusNumber())){
            throw new ResourceAlreadyExistsException("Bus number already exists: " + request.getBusNumber());
        }

//       Create bus
        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .owner(owner)
                .build();
        Bus savedBus = busRepository.save(bus);

        log.info("Owner {} created bus: {}", owner.getUsername(), savedBus.getBusNumber());

        return mapToResponseDTO(savedBus);


    }

//    Get all buses accessible to user
//    OWNER: Gets all their buses
//    CONDUCTOR: Gets only assigned buses

    @Transactional(readOnly = true)
    public List<BusResponseDTO> getMyBuses(User user) {

        if (user.getRole() == Role.OWNER) {
            // Owner gets all their buses
            List<Bus> buses = busRepository.findByOwnerId(user.getId());
            return buses.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor gets only assigned buses
            List<Bus> buses = busAssignmentRepository
                    .findByUserIdAndIsActiveTrue(user.getId())
                    .stream()
                    .map(BusAssignment::getBus)
                    .toList();

            return buses.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        }

        return List.of(); // Empty list for other roles
    }

//     Update bus details (OWNER only)

    @Transactional
    public BusResponseDTO updateBus(Long busId, BusRequestDTO request, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can update buses");
        }

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        // Verify ownership
        if (!bus.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own this bus");
        }

        // Check if new bus number is taken by another bus
        if (!bus.getBusNumber().equals(request.getBusNumber())
                && busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new IllegalArgumentException("Bus number already exists: " + request.getBusNumber());
        }

        // Update bus number
        bus.setBusNumber(request.getBusNumber());

        Bus updatedBus = busRepository.save(bus);

        log.info("Owner {} updated bus {} to {}",
                owner.getUsername(), busId, updatedBus.getBusNumber());

        return mapToResponseDTO(updatedBus);
    }


//    Delete bus (OWNER only)
//    Also deactivates all assignments

    @Transactional
    public void deleteBus(Long busId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can delete buses");
        }

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        // Verify ownership
        if (!bus.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own this bus");
        }

        // Deactivate all assignments for this bus
        busAssignmentRepository.findByBusIdAndIsActiveTrue(busId)
                .forEach(assignment -> {
                    assignment.setIsActive(false);
                    busAssignmentRepository.save(assignment);
                });

        // Delete the bus
        busRepository.delete(bus);

        log.info("Owner {} deleted bus: {}", owner.getUsername(), bus.getBusNumber());
    }




    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Validate if user has access to the bus
     */
    private void validateBusAccess(User user, Long busId) {
        if (user.getRole() == Role.OWNER) {
            // Owner must own the bus
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

            if (!bus.getOwner().getId().equals(user.getId())) {
                throw new UnauthorizedException("You don't own this bus");
            }
        } else if (user.getRole() == Role.CONDUCTOR) {
            // Conductor must have active assignment
            boolean hasAccess = busAssignmentRepository
                    .existsByUserIdAndBusIdAndIsActiveTrue(user.getId(), busId);

            if (!hasAccess) {
                throw new UnauthorizedException("You don't have access to this bus");
            }
        } else {
            throw new UnauthorizedException("Invalid role");
        }
    }


    /**
     * Map Bus entity to BusResponseDTO
     */
    private BusResponseDTO mapToResponseDTO(Bus bus) {

        // Get active conductors count
        int activeConductorsCount = busAssignmentRepository
                .findByBusIdAndIsActiveTrue(bus.getId())
                .size();

        return BusResponseDTO.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .ownerUsername(bus.getOwner().getUsername())
                .activeConductorsCount(activeConductorsCount)
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .build();
    }

}
