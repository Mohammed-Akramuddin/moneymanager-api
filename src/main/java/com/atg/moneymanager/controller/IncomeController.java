package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@RestController
@RequestMapping("/income")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @PostMapping
    public ResponseEntity<IncomeDTO> saveIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO incomeDTO1 = incomeService.saveIncome(incomeDTO);
        return ResponseEntity.ok(incomeDTO1);
    }
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getAllIncomes() {
        List<IncomeDTO> ans=incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(ans);
    }
    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return "success";
    }

//    @GetMapping("/excel")
//    public ResponseEntity<byte[]> exportToExcel() {
//        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomeForCurrentUser();
//
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Incomes");
//
//            // Header row
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("Name");
//            headerRow.createCell(1).setCellValue("Category");
//            headerRow.createCell(2).setCellValue("Date");
//            headerRow.createCell(3).setCellValue("Amount");
//
//            // Data rows
//            int rowNum = 1;
//            for (IncomeDTO income : incomes) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(income.getName());
//                row.createCell(1).setCellValue(income.getCategoryName());
//                row.createCell(2).setCellValue(income.getDate().toString());
//                row.createCell(3).setCellValue(income.getAmount().toString());
//            }
//
//            // Write to byte array
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            workbook.write(out);
//            byte[] bytes = out.toByteArray();
//
//            // Prepare headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=incomes.xlsx");
//            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(bytes);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//    }


}
