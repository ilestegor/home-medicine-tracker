package org.ilestegor.home_medicine_tracker.application.port.in.dashboard;

import org.ilestegor.home_medicine_tracker.application.port.in.dashboard.model.TodayDose;

import java.util.List;

public interface GetTodayPlanQuery {

    List<TodayDose> today();
}
