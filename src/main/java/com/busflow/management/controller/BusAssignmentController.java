package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.AssignBusRequestDTO;
import com.busflow.management.dto.BusAssignmentResponseDTO;
import com.busflow.management.service.BusAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buses/bus-assignments")
@PreAuthorize("hasRole('OWNER')")
@Slf4j
public class BusAssignmentController {

    private final BusAssignmentService busAssignmentService;

    /**
    *Assign conductor to bus
     */
    @PostMapping
    public ResponseEntity<BusAssignmentResponseDTO> assignConductorToBus(
            @Valid @RequestBody AssignBusRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails

    ) {
        BusAssignmentResponseDTO response = busAssignmentService.assignConductorToBus(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


    /**
     * Get all assignments (for current owner)
     */
    @GetMapping
    public ResponseEntity<List<BusAssignmentResponseDTO>> getAllMyAssignments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("📝 GET /api/bus-assignments - Getting all assignments for owner");

        List<BusAssignmentResponseDTO> assignments = busAssignmentService
                .getAllMyAssignments(userDetails.getUser());

        return ResponseEntity.ok(assignments);
    }


    /**
     * Get all assignments for a specific bus
     */
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<BusAssignmentResponseDTO>> getAssignmentsForBus(
            @PathVariable Long busId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("📝 GET /api/bus-assignments/bus/{} - activeOnly: {}", busId, activeOnly);

        List<BusAssignmentResponseDTO> assignments;

        if (activeOnly) {
            assignments = busAssignmentService.getActiveAssignmentsForBus(busId, userDetails.getUser());
        } else {
            assignments = busAssignmentService.getAssignmentsForBus(busId, userDetails.getUser());
        }

        return ResponseEntity.ok(assignments);
    }

    /**
     * Get all assignments for a specific conductor
     */
    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<List<BusAssignmentResponseDTO>> getAssignmentsForConductor(
            @PathVariable Long conductorId,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("📝 GET /api/bus-assignments/conductor/{} - activeOnly: {}", conductorId, activeOnly);

        List<BusAssignmentResponseDTO> assignments;

        if (activeOnly) {
            assignments = busAssignmentService.getActiveAssignmentsForConductor(conductorId, userDetails.getUser());
        } else {
            assignments = busAssignmentService.getAssignmentsForConductor(conductorId, userDetails.getUser());
        }

        return ResponseEntity.ok(assignments);
    }


    /**
     * Revoke assignment (deactivate)
     */
    @PutMapping("/{assignmentId}/revoke")
    public ResponseEntity<String> revokeAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("📝 PUT /api/bus-assignments/{}/revoke", assignmentId);

        busAssignmentService.revokeAssignment(assignmentId, userDetails.getUser());
        return ResponseEntity.ok("Assignment revoked successfully");
    }


    /**
       * Delete assignment permanently
     */
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<String> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("📝 DELETE /api/bus-assignments/{}", assignmentId);

        busAssignmentService.deleteAssignment(assignmentId, userDetails.getUser());
        return ResponseEntity.ok("Assignment deleted successfully");
    }
}
