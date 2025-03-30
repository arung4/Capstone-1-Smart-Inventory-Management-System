package com.example.sims.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sims.model.Alert;
import com.example.sims.model.InventoryItem;
import com.example.sims.repository.AlertRepository;
import com.example.sims.repository.InventoryRepository;

@Service
public class ReportService {
    private final InventoryRepository itemRepository;
    private final AlertRepository alertRepository;

    @Autowired
    public ReportService(InventoryRepository itemRepository,
                         AlertRepository alertRepository) {
        this.itemRepository = itemRepository;
        this.alertRepository = alertRepository;
    }

    // Daily stock report
    public byte[] generateDailyReport() {
        LocalDate today = LocalDate.now();
        return generateStockMovementReport(today, today);
    }

    // Weekly stock report
    public byte[] generateWeeklyReport() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days
        return generateStockMovementReport(startDate, endDate);
    }

    // Custom date range report
    public byte[] generateCustomReport(LocalDate startDate, LocalDate endDate) {
        return generateStockMovementReport(startDate, endDate);
    }

    private byte[] generateStockMovementReport(LocalDate startDate, LocalDate endDate) {
        List<InventoryItem> items = itemRepository.findByLastUpdatedBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        List<Alert> alerts = alertRepository.findByCreatedAtBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        StringBuilder csv = new StringBuilder();
        // CSV Header
        csv.append("Date,Item Name,Category,Current Quantity,Price,Status\n");

        // Add items
        items.forEach(item -> {
            csv.append(item.getLastUpdated().toLocalDate())
                    .append(",")
                    .append(escapeCsv(item.getName()))
                    .append(",")
                    .append(escapeCsv(item.getCategory()))
                    .append(",")
                    .append(item.getQuantity())
                    .append(",")
                    .append(item.getPrice())
                    .append(",")
                    .append(getItemStatus(item))
                    .append("\n");
        });

        // Add alerts if needed
        alerts.forEach(alert -> {
            csv.append(alert.getCreatedAt().toLocalDate())
                    .append(",ALERT: ")
                    .append(escapeCsv(alert.getMessage()))
                    .append(",,,,,\n");
        });

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return "\"" + input.replace("\"", "\"\"") + "\"";
    }

    private String getItemStatus(InventoryItem item) {
        if (item.getQuantity() < 5) return "LOW_STOCK";
        if (item.getExpiryDate() != null &&
                item.getExpiryDate().isBefore(LocalDate.now().plusDays(7))) {
            return "NEAR_EXPIRY";
        }
        return "OK";
    }


    // Get Reports in CSV format
    public List<Map<String, Object>> generateStockMovementReportJson(LocalDate startDate, LocalDate endDate) {
        List<InventoryItem> items = itemRepository.findByLastUpdatedBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        List<Alert> alerts = alertRepository.findByCreatedAtBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        List<Map<String, Object>> reportData = new ArrayList<>();

        items.forEach(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", item.getLastUpdated().toLocalDate().toString());
            row.put("name", item.getName());
            row.put("category", item.getCategory());
            row.put("quantity", item.getQuantity());
            row.put("price", item.getPrice());
            row.put("status", getItemStatus(item));
            reportData.add(row);
        });

        alerts.forEach(alert -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", alert.getCreatedAt().toLocalDate().toString());
            row.put("name", "ALERT: " + alert.getMessage());
            row.put("category", "ALERT");
            row.put("quantity", null);
            row.put("price", null);
            row.put("status", alert.getType().toString());
            reportData.add(row);
        });

        return reportData;
    }
}
