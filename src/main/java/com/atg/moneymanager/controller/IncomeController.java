package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
