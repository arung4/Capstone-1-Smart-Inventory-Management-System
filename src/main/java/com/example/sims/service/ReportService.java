package com.example.sims.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.sims.model.StockMovement;
import com.example.sims.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sims.model.Alert;
import com.example.sims.model.InventoryItem;
import com.example.sims.repository.AlertRepository;
import com.example.sims.repository.InventoryRepository;

@Service
public class ReportService {
    private final StockMovementRepository stockMovementRepository;

    @Autowired
    public ReportService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    // CSV Report Generators

    // Daily stock report
    public byte[] generateDailyStockMovementReportCSV() {
        LocalDate today = LocalDate.now();
        return generateStockMovementReportCSV(today, today);
    }

    // Weekly stock report
    public byte[] generateWeeklyStockMovementReportCSV() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days
        return generateStockMovementReportCSV(startDate, endDate);
    }

    // Custom date range report
    public byte[] generateCustomStockMovementReportCSV(LocalDate startDate, LocalDate endDate) {
        return generateStockMovementReportCSV(startDate, endDate);
    }

    private byte[] generateStockMovementReportCSV(LocalDate startDate, LocalDate endDate) {

    List<StockMovement> movements = stockMovementRepository.findByMovementDateBetween(
            startDate.atStartOfDay(),
            endDate.atTime(23,59,59)
    );
      StringBuilder csv = new StringBuilder();

      // CSV Header
        csv.append("Date, Item, Movement Type, Quantity Change, Previous Quantity, New Quantity, User, Notes\n");

        // Add Movements
        movements.forEach(movement -> {
            csv.append(movement.getMovementDate().toLocalDate())
                    .append(",")
                    .append(escapeCsv(movement.getInventoryItem().getName()))
                    .append(",")
                    .append(movement.getMovementType())
                    .append(",")
                    .append(movement.getQuantityChange())
                    .append(",")
                    .append(movement.getPreviousQuantity())
                    .append(",")
                    .append(movement.getNewQuantity())
                    .append(",")
                    .append(escapeCsv(movement.getUser().getEmail()))
                    .append(",")
                    .append(escapeCsv(movement.getNotes()))
                    .append("\n");
        });

         return csv.toString().getBytes(StandardCharsets.UTF_8);
    }


   // JSON Report Generators
   public List<Map<String, Object>> generateDailyStockMovementReportJson() {
       LocalDate today = LocalDate.now();
       return generateStockMovementReportJson(today, today);
   }

    public List<Map<String, Object>> generateWeeklyStockMovementReportJson() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days
        return generateStockMovementReportJson(startDate, endDate);
    }

    public List<Map<String, Object>> generateCustomStockMovementReportJson(LocalDate startDate, LocalDate endDate) {
        return generateStockMovementReportJson(startDate, endDate);
    }

    private List<Map<String, Object>> generateStockMovementReportJson(LocalDate startDate, LocalDate endDate) {
        List<StockMovement> movements = stockMovementRepository.findByMovementDateBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return movements.stream().map(movement -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", movement.getMovementDate().toString());
            row.put("itemName", movement.getInventoryItem().getName());
            row.put("movementType", movement.getMovementType());
            row.put("quantityChange", movement.getQuantityChange());
            row.put("previousQuantity", movement.getPreviousQuantity());
            row.put("newQuantity", movement.getNewQuantity());
            row.put("user", movement.getUser().getEmail());
            row.put("notes", movement.getNotes());
            return row;
        }).collect(Collectors.toList());
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return "\"" + input.replace("\"", "\"\"") + "\"";
    }

}
