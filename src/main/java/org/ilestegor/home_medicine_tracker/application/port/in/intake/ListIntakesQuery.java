package org.ilestegor.home_medicine_tracker.application.port.in.intake;

import org.ilestegor.home_medicine_tracker.application.common.PageRequest;
import org.ilestegor.home_medicine_tracker.application.common.PageResult;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter;
import org.ilestegor.home_medicine_tracker.domain.model.IntakeRecord;

public interface ListIntakesQuery {

    PageResult<IntakeRecord> list(IntakeFilter filter, PageRequest page);
}
