package org.ilestegor.home_medicine_tracker.adapter.out.persistence.projection;

import java.math.BigDecimal;

public record MedicineStockTotal(Long medicineId, BigDecimal available) {
}
