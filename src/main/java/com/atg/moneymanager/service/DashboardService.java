package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.IncomeEntity;
import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.dto.ExpenseDTO;
import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.dto.RecentTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.DoubleStream.concat;

@Service
public class DashboardService {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private ExpenseService expenseService;

    public Map<String,Object> getDashboardData(){
        Map<String,Object>map=new LinkedHashMap<>();
        List<IncomeDTO> incomes=incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> expenses=expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionDTO> recentTransaction = Stream.concat(
                        incomes.stream().map(income ->
                                RecentTransactionDTO.builder()
                                        .id(income.getId())
                                        .profileId(income.getProfile_id())
                                        .name(income.getName())
                                        .amount(income.getAmount())
                                        .type("income")
                                        .date(income.getDate())
                                        .icon(income.getIcon())
                                        .createdAt(income.getCreateAt())
                                        .updatedAt(income.getUpdateAt())
                                        .build()
                        ),
                        expenses.stream().map(expense ->
                                RecentTransactionDTO.builder()
                                        .id(expense.getId())
                                        .profileId(expense.getProfile_id())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .type("expense")
                                        .date(expense.getDate())
                                        .icon(expense.getIcon())
                                        .createdAt(expense.getCreateAt())
                                        .updatedAt(expense.getUpdateAt())
                                        .build()
                        )
                )
                .sorted((RecentTransactionDTO a, RecentTransactionDTO b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).toList();

        map.put("totalBalance",incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        map.put("totalIncome",incomeService.getTotalIncomesForCurrentUser());
        map.put("totalExpense",expenseService.getTotalExpensesForCurrentUser());
        map.put("recent5Incomes",incomes);
        map.put("recent5Expenses",expenses);
        map.put("recentTransactions",recentTransaction);
        return map;

    }
}
