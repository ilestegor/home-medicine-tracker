package org.ilestegor.home_medicine_tracker.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.MedicineRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.MedicineResponse;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.CreateMedicineUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.DeleteMedicineUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.GetMedicineQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.ListMedicinesQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.medicine.UpdateMedicineUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final CreateMedicineUseCase createMedicine;
    private final UpdateMedicineUseCase updateMedicine;
    private final DeleteMedicineUseCase deleteMedicine;
    private final GetMedicineQuery getMedicine;
    private final ListMedicinesQuery listMedicines;

    @GetMapping
    public List<MedicineResponse> list(@RequestParam(required = false) String q) {
        return listMedicines.search(q).stream().map(MedicineResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MedicineResponse get(@PathVariable Long id) {
        return MedicineResponse.from(getMedicine.byId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineResponse create(@Valid @RequestBody MedicineRequest request) {
        return MedicineResponse.from(createMedicine.create(request.toCreateCommand()));
    }

    @PutMapping("/{id}")
    public MedicineResponse update(@PathVariable Long id,
                                   @Valid @RequestBody MedicineRequest request) {
        return MedicineResponse.from(updateMedicine.update(request.toUpdateCommand(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteMedicine.delete(id);
    }
}
