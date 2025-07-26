package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.CategoryEntity;
import com.atg.moneymanager.Model.ExpenseEntity;
import com.atg.moneymanager.Model.IncomeEntity;
import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.dto.ExpenseDTO;
import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.repository.CategoryRepository;
import com.atg.moneymanager.repository.ExpenseRepository;
import com.atg.moneymanager.repository.IncomeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProfileService profileService;

    public ExpenseDTO saveExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile=profileService.getCurrentProfile();
        Optional<CategoryEntity> category=categoryRepository.findById(expenseDTO.getCategory_id());
        ExpenseEntity expenseEntity=toEntity(expenseDTO,profile,category.get());
        expenseEntity=expenseRepository.save(expenseEntity);
        return toDTO(expenseEntity);
    }


    public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser() {
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> ans=expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return ans.stream().map(this::toDTO).collect(Collectors.toList());
    }
    public void deleteExpense(Long id) {
        ProfileEntity profile=profileService.getCurrentProfile();
        ExpenseEntity expenseEntity=expenseRepository.findById(id).get();
        if(!expenseEntity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized User");
        }
        expenseRepository.delete(expenseEntity);
    }
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> ans=expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return ans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BigDecimal getTotalExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal ans=expenseRepository.findAmountByProfileId(profile.getId());
        return ans != null ? ans : BigDecimal.ZERO;
    }
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String Keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> ans=expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,Keyword,sort);
        return ans.stream().map(this::toDTO).toList();
    }

    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> ans=expenseRepository.findByProfileIdAndDate(profileId,date);
        return ans.stream().map(this::toDTO).collect(Collectors.toList());
    }
    public ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .amount(expenseDTO.getAmount())
                .category(category)
                .profile(profile)
                .icon(expenseDTO.getIcon())
                .name(expenseDTO.getName())
                .build();
    }
    public ExpenseDTO toDTO(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .amount(expenseEntity.getAmount())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .date(expenseEntity.getDate())
                .category_id(expenseEntity.getCategory().getId())
                .categoryName(expenseEntity.getCategory().getName())
                .profile_id(expenseEntity.getProfile().getId())
                .createAt(expenseEntity.getCreateAt())
                .updateAt(expenseEntity.getUpdateAt())
                .build();
    }
}
