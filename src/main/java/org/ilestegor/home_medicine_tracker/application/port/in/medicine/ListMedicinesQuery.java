package org.ilestegor.home_medicine_tracker.application.port.in.medicine;

import org.ilestegor.home_medicine_tracker.domain.model.Medicine;

import java.util.List;

public interface ListMedicinesQuery {

    List<Medicine> search(String query);
}
