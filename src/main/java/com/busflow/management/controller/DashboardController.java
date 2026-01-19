package com.busflow.management.controller;

import com.busflow.management.dto.DashboardResponseDTO;
//import com.busflow.management.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

//    private final DashboardService dashboardService;
//
//    public DashboardController(DashboardService dashboardService) {
//        this.dashboardService = dashboardService;
//    }
//
//    @GetMapping("/conductor/yesterday")
//    public DashboardResponseDTO conductorToday(@RequestParam Long userId) {
//        return dashboardService.getConductorYesterday(userId);
//    }
}
