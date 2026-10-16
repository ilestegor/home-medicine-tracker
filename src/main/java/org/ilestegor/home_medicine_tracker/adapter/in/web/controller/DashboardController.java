package org.ilestegor.home_medicine_tracker.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.TodayDoseResponse;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.GetDashboardSummaryQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.GetTodayPlanQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.DashboardSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetTodayPlanQuery todayPlan;
    private final GetDashboardSummaryQuery dashboardSummary;

    @GetMapping("/today")
    public List<TodayDoseResponse> today() {
        return todayPlan.today().stream().map(TodayDoseResponse::from).toList();
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardSummary.summary();
    }
}
