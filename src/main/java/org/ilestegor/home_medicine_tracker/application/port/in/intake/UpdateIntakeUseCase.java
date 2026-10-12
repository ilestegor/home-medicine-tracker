package org.ilestegor.home_medicine_tracker.application.port.in.intake;

import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.UpdateIntakeCommand;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

public interface UpdateIntakeUseCase {

    IntakeRecord update(UpdateIntakeCommand command);
}
