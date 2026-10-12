package org.ilestegor.home_medicine_tracker.application.port.in.intake;

import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.RecordIntakeCommand;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

public interface RecordIntakeUseCase {

    IntakeRecord record(RecordIntakeCommand command);
}
