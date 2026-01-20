package com.busflow.management.controller;

import com.busflow.management.config.CustomUserDetails;
import com.busflow.management.dto.AssignBusRequestDTO;
import com.busflow.management.dto.BusRequestDTO;
import com.busflow.management.dto.BusResponseDTO;
import com.busflow.management.service.BusAssignmentService;
import com.busflow.management.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buses")
@RequiredArgsConstructor

public class  BusController {

    private final BusService busService;

//    Create bus (OWNER only)
@PostMapping
//@PreAuthorize("hasRole('OWNER')")
public ResponseEntity<BusResponseDTO> createBus(
        @Valid @RequestBody BusRequestDTO request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    BusResponseDTO response = busService.createBus(request, userDetails.getUser());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

//    Get all buses (accessible to the user)
    @GetMapping
    public ResponseEntity<List<BusResponseDTO>> getMyBus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BusResponseDTO> buses = busService.getMyBuses(userDetails.getUser());
        return ResponseEntity.ok(buses);
    }

//    Get bus by id
//    @GetMapping("/{busId}")
//    public ResponseEntity<BusResponseDTO> getBusById(@PathVariable Long busId, @AuthenticationPrincipal CustomUserDetails userDetails) {
//        BusResponseDTO response = busService.getBusById(busId, userDetails.getUser());
//        return ResponseEntity.ok(response);
//    }


//  Update bus (OWNER only)
    @PutMapping("/{busId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusResponseDTO> updateBus(
        @PathVariable Long busId,
        @Valid @RequestBody BusRequestDTO request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    BusResponseDTO response = busService.updateBus(busId, request, userDetails.getUser());
    return ResponseEntity.ok(response);
    }

    // Delete bus (OWNER only)
    @DeleteMapping("/{busId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> deleteBus(
        @PathVariable Long busId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    busService.deleteBus(busId, userDetails.getUser());
    return ResponseEntity.ok("Bus deleted successfully");
}

}
