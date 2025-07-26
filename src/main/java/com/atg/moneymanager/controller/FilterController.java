package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.ExpenseDTO;
import com.atg.moneymanager.dto.FilterDTO;
import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.service.ExpenseService;
import com.atg.moneymanager.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterController {
    @Autowired
    private  IncomeService incomeService;
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO){
        LocalDate startDate=filterDTO.getStartDate() !=null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate=filterDTO.getEndDate() !=null ? filterDTO.getEndDate() : LocalDate.now();
        String Keyword=filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";
        String sortField= filterDTO.getSortField() !=null ? filterDTO.getSortField() : "date";
        Sort.Direction direction="desc".equalsIgnoreCase(filterDTO.getSortOrder())?Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
        if("income".equals(filterDTO.getType())){
            List<IncomeDTO> ans=incomeService.filterIncomes(startDate,endDate,Keyword,sort);
            return ResponseEntity.ok().body(ans);
        }
        else if("expense".equals(filterDTO.getType())) {
            List<ExpenseDTO> ans = expenseService.filterExpenses(startDate, endDate, Keyword, sort);
            return ResponseEntity.ok().body(ans);
        }else{
            return ResponseEntity.badRequest().body("Invalid Type.Please enter type in income or expense");
        }

    }
}
