package org.ilestegor.home_medicine_tracker.application.port.in.regimen;

import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.CreateRegimenCommand;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;

public interface CreateRegimenUseCase {

    Regimen create(CreateRegimenCommand command);
}
