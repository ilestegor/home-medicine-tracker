package org.ilestegor.home_medicine_tracker.application.port.in.regimen;

import org.ilestegor.home_medicine_tracker.domain.model.Regimen;

import java.util.List;

public interface FindEffectiveRegimensQuery {

    List<Regimen> today();
}
