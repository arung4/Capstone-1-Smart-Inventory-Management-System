package com.example.sims.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sims.service.ReportService;

@RestController
@RequestMapping("/api/reports/stock-movement")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Reports in CSV format
    @GetMapping("/daily/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ByteArrayResource> downloadDailyReportCSV() {
        byte[] report = reportService.generateDailyStockMovementReportCSV();
        return createReportResponse(report, "daily_stock_movement_report.csv");
    }

    @GetMapping("/weekly/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ByteArrayResource> downloadWeeklyReportCSV() {
        byte[] report = reportService.generateWeeklyStockMovementReportCSV();
        return createReportResponse(report, "weekly_stock_movement_report.csv");
    }

    @GetMapping("/custom/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ByteArrayResource> downloadCustomReportCSV(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] report = reportService.generateCustomStockMovementReportCSV(startDate,endDate);
        return createReportResponse(report,
                String.format("stock_movement_report_%s_to_%s.csv", startDate, endDate));
    }

    private ResponseEntity<ByteArrayResource> createReportResponse(byte[] report, String filename) {
        ByteArrayResource resource = new ByteArrayResource(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(report.length)
                .body(resource);
    }

    // Reports in JSON- format

    @GetMapping("/daily-json")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Map<String, Object>>> getDailyReportJson() {
        return ResponseEntity.ok(reportService.generateDailyStockMovementReportJson());
    }

    @GetMapping("/weekly-json")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyReportJson() {
        return ResponseEntity.ok(reportService.generateWeeklyStockMovementReportJson(
        ));
    }

    @GetMapping("/custom-json")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Map<String, Object>>> getCustomReportJson(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateCustomStockMovementReportJson(
                startDate,
                endDate
        ));
    }
}
