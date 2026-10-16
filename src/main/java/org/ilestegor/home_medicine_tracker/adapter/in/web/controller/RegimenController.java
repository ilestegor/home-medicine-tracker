package org.ilestegor.home_medicine_tracker.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.RegimenRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.RegimenResponse;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.CreateRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.DeleteRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.FindEffectiveRegimensQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.GetRegimenQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.ListRegimensQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.UpdateRegimenUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.regimen.command.RegimenFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.domain.model.Regimen;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/regimens")
@RequiredArgsConstructor
public class RegimenController {

    private final CreateRegimenUseCase createRegimen;
    private final UpdateRegimenUseCase updateRegimen;
    private final DeleteRegimenUseCase deleteRegimen;
    private final GetRegimenQuery getRegimen;
    private final ListRegimensQuery listRegimens;
    private final FindEffectiveRegimensQuery findEffective;
    private final ClockPort clock;

    @GetMapping
    public List<RegimenResponse> list(@RequestParam(required = false) Long medicineId,
                                      @RequestParam(required = false) Boolean active) {
        return toResponses(listRegimens.list(new RegimenFilter(medicineId, active)));
    }

    @GetMapping("/effective-today")
    public List<RegimenResponse> effectiveToday() {
        return toResponses(findEffective.today());
    }

    @GetMapping("/{id}")
    public RegimenResponse get(@PathVariable Long id) {
        return toResponse(getRegimen.byId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegimenResponse create(@Valid @RequestBody RegimenRequest request) {
        return toResponse(createRegimen.create(request.toCreateCommand()));
    }

    @PutMapping("/{id}")
    public RegimenResponse update(@PathVariable Long id,
                                  @Valid @RequestBody RegimenRequest request) {
        return toResponse(updateRegimen.update(request.toUpdateCommand(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteRegimen.delete(id);
    }

    private RegimenResponse toResponse(Regimen r) {
        return RegimenResponse.from(r, clock.today());
    }

    private List<RegimenResponse> toResponses(List<Regimen> regimens) {
        LocalDate today = clock.today();
        return regimens.stream().map(r -> RegimenResponse.from(r, today)).toList();
    }
}
