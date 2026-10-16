package org.ilestegor.home_medicine_tracker.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.PageResponse;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.IntakeRecordRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.IntakeRecordResponse;
import org.ilestegor.home_medicine_tracker.application.common.PageRequest;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.DeleteIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.GetIntakeQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.ListIntakesQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.RecordIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.UpdateIntakeUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.intake.command.IntakeFilter;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/intakes")
@RequiredArgsConstructor
public class IntakeController {

    private final RecordIntakeUseCase recordIntake;
    private final UpdateIntakeUseCase updateIntake;
    private final DeleteIntakeUseCase deleteIntake;
    private final GetIntakeQuery getIntake;
    private final ListIntakesQuery listIntakes;

    @GetMapping
    public PageResponse<IntakeRecordResponse> list(
            @RequestParam(required = false) Long stockItemId,
            @RequestParam(required = false) Long regimenId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        return PageResponse.of(
                listIntakes.list(new IntakeFilter(stockItemId, regimenId, from, to),
                        PageRequest.of(page, size)),
                IntakeRecordResponse::from);
    }

    @GetMapping("/{id}")
    public IntakeRecordResponse get(@PathVariable Long id) {
        return IntakeRecordResponse.from(getIntake.byId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntakeRecordResponse create(@Valid @RequestBody IntakeRecordRequest request) {
        return IntakeRecordResponse.from(recordIntake.record(request.toRecordCommand()));
    }

    @PutMapping("/{id}")
    public IntakeRecordResponse update(@PathVariable Long id,
                                       @Valid @RequestBody IntakeRecordRequest request) {
        return IntakeRecordResponse.from(updateIntake.update(request.toUpdateCommand(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteIntake.delete(id);
    }
}
