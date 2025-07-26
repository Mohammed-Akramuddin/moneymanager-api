package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.ExpenseDTO;

import com.atg.moneymanager.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    @PostMapping
    public ResponseEntity<ExpenseDTO> saveExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO expenseDTO1=expenseService.saveExpense(expenseDTO);
        return ResponseEntity.ok(expenseDTO1);
    }
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllIncomes() {
        List<ExpenseDTO> ans=expenseService.getCurrentMonthExpenseForCurrentUser();
        return ResponseEntity.ok(ans);
    }
    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "success";
    }
}
