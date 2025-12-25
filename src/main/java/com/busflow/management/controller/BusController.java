package com.busflow.management.controller;

import com.busflow.management.dto.AssignBusRequestDTO;
import com.busflow.management.service.BusAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusAssignmentService busAssignmentService;

    @PostMapping("/assign-conductor")
    public ResponseEntity<String> assignConductorToBus(
            @RequestBody AssignBusRequestDTO request) {

        busAssignmentService.assignConductorToBus(request);
        return ResponseEntity.ok("Conductor assigned to bus successfully");
    }
}
