package com.busflow.management.service;

import com.busflow.management.dto.AssignBusRequestDTO;
import com.busflow.management.dto.BusAssignmentResponseDTO;
import com.busflow.management.entity.Bus;
import com.busflow.management.entity.BusAssignment;
import com.busflow.management.entity.User;
import com.busflow.management.enums.Role;
import com.busflow.management.exception.InvalidCredentialsException;
import com.busflow.management.exception.ResourceAlreadyExistsException;
import com.busflow.management.exception.ResourceNotFoundException;
import com.busflow.management.exception.UnauthorizedException;
import com.busflow.management.repository.BusAssignmentRepository;
import com.busflow.management.repository.BusRepository;
import com.busflow.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusAssignmentService {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BusAssignmentRepository busAssignmentRepository;

    /**
     * Assign conductor to bus (OWNER only)
     */
    @Transactional
    public BusAssignmentResponseDTO assignConductorToBus(AssignBusRequestDTO request, User owner){

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owner can assign conductors to buses");
        }

        // Get bus and verify ownership
        Bus bus = busRepository.findById(request.getBusId()).orElseThrow(()-> new ResourceNotFoundException("Bus not found with id: " + request.getBusId()));

        if (!bus.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You`re not belongs to this bus");
        }

        // Get conductor and verify role
        User conductor = userRepository.findById(request.getConductorId()).orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + request.getConductorId()));

        if (!conductor.getRole().equals(Role.CONDUCTOR)) {
            throw new UnauthorizedException("User is not a conductor");
        }

        // Check if assignment already exists
        boolean alreadyAssigned = busAssignmentRepository.existsByUserIdAndBusIdAndIsActiveTrue(request.getBusId(), bus.getId());

        if (alreadyAssigned) {
            throw new ResourceAlreadyExistsException("Conductor is already assigned to bus : " + bus.getBusNumber()  );
        }

//      Create new assign
        BusAssignment assignment = BusAssignment.builder()
                .user(conductor)
                .bus(bus)
                .assignedBy(owner)
                .isActive(true)
                .assignedDate(LocalDateTime.now())
                .build();

        BusAssignment savedAssignment = busAssignmentRepository.save(assignment);

        log.info("Owner {} assigned conductor {} to bus {}",
                owner.getUsername(), conductor.getUsername(), bus.getBusNumber());

        return mapToResponseDTO(savedAssignment);
    }



    /**
     * Get all assignments for a specific bus (OWNER only)
     */
    @Transactional(readOnly = true)
    public List<BusAssignmentResponseDTO> getAssignmentsForBus(Long busId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view bus assignments");
        }

        // Get bus and verify ownership
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        if (!bus.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own this bus");
        }

        // Get all assignments (active and inactive)
        List<BusAssignment> assignments = busAssignmentRepository.findByBusId(busId);

        return assignments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get only active assignments for a specific bus (OWNER only)
     */
    @Transactional(readOnly = true)
    public List<BusAssignmentResponseDTO> getActiveAssignmentsForBus(Long busId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view bus assignments");
        }

        // Get bus and verify ownership
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        if (!bus.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own this bus");
        }

        // Get active assignments only
        List<BusAssignment> assignments = busAssignmentRepository.findByBusIdAndIsActiveTrue(busId);

        return assignments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
        * Get all assignments for a specific conductor (OWNER only)
     */
    @Transactional(readOnly = true)
    public List<BusAssignmentResponseDTO> getAssignmentsForConductor(Long conductorId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view conductor assignments");
        }

        // Get conductor
        User conductor = userRepository.findById(conductorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + conductorId));

        if (conductor.getRole() != Role.CONDUCTOR) {
            throw new IllegalArgumentException("User is not a conductor");
        }

        // Get all assignments for this conductor
        List<BusAssignment> assignments = busAssignmentRepository.findByUserId(conductorId);

        // Filter to only show buses owned by this owner
        return assignments.stream()
                .filter(assignment -> assignment.getBus().getOwner().getId().equals(owner.getId()))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete assignment permanently
     */
    @Transactional
    public void deleteAssignment(Long assignmentId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can delete assignments");
        }

        BusAssignment assignment = busAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        // Verify ownership
        if (!assignment.getBus().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own the bus for this assignment");
        }

        busAssignmentRepository.delete(assignment);

        log.info("Owner {} deleted assignment {} (conductor {} from bus {})",
                owner.getUsername(), assignmentId,
                assignment.getUser().getUsername(), assignment.getBus().getBusNumber());
    }

    /**
     * Get all assignments for the owner
     */
    @Transactional(readOnly = true)
    public List<BusAssignmentResponseDTO> getAllMyAssignments(User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view assignments");
        }

        // Get all buses owned by this owner
        List<Bus> ownedBuses = busRepository.findByOwnerId(owner.getId());
        List<Long> busIds = ownedBuses.stream()
                .map(Bus::getId)
                .collect(Collectors.toList());

        // Get all assignments for these buses
        List<BusAssignment> assignments = busAssignmentRepository.findByBusIdIn(busIds);

        return assignments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active assignments for a conductor
     */
    @Transactional(readOnly = true)
    public List<BusAssignmentResponseDTO> getActiveAssignmentsForConductor(Long conductorId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can view conductor assignments");
        }

        // Get conductor
        User conductor = userRepository.findById(conductorId)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor not found with id: " + conductorId));

        if (conductor.getRole() != Role.CONDUCTOR) {
            throw new IllegalArgumentException("User is not a conductor");
        }

        // Get active assignments
        List<BusAssignment> assignments = busAssignmentRepository.findByUserIdAndIsActiveTrue(conductorId);

        // Filter to only show buses owned by this owner
        return assignments.stream()
                .filter(assignment -> assignment.getBus().getOwner().getId().equals(owner.getId()))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Revoke assignment (deactivate)
     */
    @Transactional
    public void revokeAssignment(Long assignmentId, User owner) {

        // Validate owner role
        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can revoke assignments");
        }

        BusAssignment assignment = busAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        // Verify ownership
        if (!assignment.getBus().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You don't own the bus for this assignment");
        }

        if (!assignment.getIsActive()) {
            throw new IllegalArgumentException("Assignment is already inactive");
        }

        assignment.setIsActive(false);
        assignment.setRevokedDate(LocalDateTime.now());
        busAssignmentRepository.save(assignment);

        log.info("Owner {} revoked assignment {} (conductor {} from bus {})",
                owner.getUsername(), assignmentId,
                assignment.getUser().getUsername(), assignment.getBus().getBusNumber());
    }




    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Map BusAssignment entity to BusAssignmentResponseDTO
     */
    private BusAssignmentResponseDTO mapToResponseDTO(BusAssignment assignment) {
        return BusAssignmentResponseDTO.builder()
                .id(assignment.getId())
                .conductorId(assignment.getUser().getId())
                .conductorUsername(assignment.getUser().getUsername())
                .busId(assignment.getBus().getId())
                .busNumber(assignment.getBus().getBusNumber())
                .assignedByUsername(assignment.getAssignedBy() != null ?
                        assignment.getAssignedBy().getUsername() : null)
                .isActive(assignment.getIsActive())
                .assignedDate(assignment.getAssignedDate())
                .revokedDate(assignment.getRevokedDate())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}

