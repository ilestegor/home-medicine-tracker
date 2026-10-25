package org.ilestegor.home_medicine_tracker.application.event;

import org.ilestegor.home_medicine_tracker.domain.model.MedicineForm;

public final class BusinessMetricEvents {

    private BusinessMetricEvents() {}

    public record IntakeRecorded(MedicineForm form){}

    public record LoginSucceeded() {}

    public record LoginFailed(LoginFailureReason loginFailureReason){}

    public record UserRegistered() {}

    public record StockDiscarded() {}

    public enum LoginFailureReason {
        UNKNOWN_EMAIL, BAD_PASSWORD
    }

}
