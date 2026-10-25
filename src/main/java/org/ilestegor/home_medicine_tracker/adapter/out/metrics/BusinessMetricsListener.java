package org.ilestegor.home_medicine_tracker.adapter.out.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.application.event.BusinessMetricEvents;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class BusinessMetricsListener {

    private final MeterRegistry registry;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIntake(BusinessMetricEvents.IntakeRecorded event){
        registry.counter( "business.intake.recorded", "medicine_form", event.form().name().toLowerCase(Locale.ROOT)).increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLoginSucceeded(BusinessMetricEvents.LoginSucceeded event) {
        registry.counter("business.auth.login", "result", "success", "reason", "none").increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onLoginFailed(BusinessMetricEvents.LoginFailed event) {
        registry.counter("business.auth.login", "result", "failure", "reason", event.loginFailureReason().name().toLowerCase(Locale.ROOT)).increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(BusinessMetricEvents.UserRegistered event) {
        registry.counter("business.user.registered").increment();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStockDiscarded(BusinessMetricEvents.StockDiscarded event) {
        registry.counter("business.stock.discarded").increment();
    }

}
