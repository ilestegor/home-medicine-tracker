package org.ilestegor.home_medicine_tracker.application.port.in.regimen.command;

public record RegimenFilter(Long medicineId, Boolean active) {
}
