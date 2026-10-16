package org.ilestegor.home_medicine_tracker.adapter.in.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.StockItemRequest;
import org.ilestegor.home_medicine_tracker.adapter.in.web.dto.StockItemResponse;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.AddStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.DeleteStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.DiscardStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.FindExpiredStockQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.FindExpiringStockQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.FindLowStockQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.GetStockItemQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.ListStockItemsQuery;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.UpdateStockItemUseCase;
import org.ilestegor.home_medicine_tracker.application.port.in.stock.command.StockFilter;
import org.ilestegor.home_medicine_tracker.application.port.out.ClockPort;
import org.ilestegor.home_medicine_tracker.domain.model.StockItem;
import org.ilestegor.home_medicine_tracker.domain.model.StockStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-items")
@Validated
@RequiredArgsConstructor
public class StockItemController {

    private final AddStockItemUseCase addStockItem;
    private final UpdateStockItemUseCase updateStockItem;
    private final DiscardStockItemUseCase discardStockItem;
    private final DeleteStockItemUseCase deleteStockItem;
    private final GetStockItemQuery getStockItem;
    private final ListStockItemsQuery listStockItems;
    private final FindExpiringStockQuery findExpiring;
    private final FindExpiredStockQuery findExpired;
    private final FindLowStockQuery findLowStock;

    private final ClockPort clock;

    @GetMapping
    public List<StockItemResponse> list(@RequestParam(required = false) Long medicineId,
                                        @RequestParam(required = false) StockStatus status) {
        return toResponses(listStockItems.list(new StockFilter(medicineId, status)));
    }

    @GetMapping("/expiring")
    public List<StockItemResponse> expiring(
            @RequestParam(defaultValue = "30") @Min(0) @Max(3650) int days) {
        return toResponses(findExpiring.within(days));
    }

    @GetMapping("/expired")
    public List<StockItemResponse> expired() {
        return toResponses(findExpired.expired());
    }

    @GetMapping("/low-stock")
    public List<StockItemResponse> lowStock(
            @RequestParam(defaultValue = "5") @DecimalMin("0.01") BigDecimal below) {
        return toResponses(findLowStock.below(below));
    }

    @GetMapping("/{id}")
    public StockItemResponse get(@PathVariable Long id) {
        return toResponse(getStockItem.byId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockItemResponse create(@Valid @RequestBody StockItemRequest request) {
        return toResponse(addStockItem.add(request.toAddCommand()));
    }

    @PutMapping("/{id}")
    public StockItemResponse update(@PathVariable Long id,
                                    @Valid @RequestBody StockItemRequest request) {
        return toResponse(updateStockItem.update(request.toUpdateCommand(id)));
    }

    @PostMapping("/{id}/discard")
    public StockItemResponse discard(@PathVariable Long id) {
        return toResponse(discardStockItem.discard(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteStockItem.delete(id);
    }

    private StockItemResponse toResponse(StockItem item) {
        return StockItemResponse.from(item, clock.today());
    }

    private List<StockItemResponse> toResponses(List<StockItem> items) {
        LocalDate today = clock.today();
        return items.stream().map(i -> StockItemResponse.from(i, today)).toList();
    }
}
