package org.ilestegor.home_medicine_tracker.application.port.in.intake;

import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

public interface GetIntakeQuery {

    IntakeRecord byId(Long id);
}
