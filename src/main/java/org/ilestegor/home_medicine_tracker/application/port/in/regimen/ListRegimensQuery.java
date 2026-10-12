package org.ilestegor.home_medicine_tracker.application.port.in.regimen;

import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;

import java.util.List;

public interface ListRegimensQuery {

    List<Regimen> list(RegimenFilter filter);
}
