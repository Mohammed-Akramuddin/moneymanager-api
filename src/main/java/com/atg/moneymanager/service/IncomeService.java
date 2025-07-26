package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.CategoryEntity;


import com.atg.moneymanager.Model.ExpenseEntity;
import com.atg.moneymanager.Model.IncomeEntity;
import com.atg.moneymanager.Model.ProfileEntity;

import com.atg.moneymanager.dto.ExpenseDTO;
import com.atg.moneymanager.dto.IncomeDTO;
import com.atg.moneymanager.repository.CategoryRepository;
import com.atg.moneymanager.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProfileService profileService;
    public IncomeDTO saveIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile=profileService.getCurrentProfile();
        Optional<CategoryEntity> category=categoryRepository.findById(incomeDTO.getCategory_id());
        IncomeEntity incomeEntity=toEntity(incomeDTO,profile,category.get());
        incomeEntity=incomeRepository.save(incomeEntity);
        return toDTO(incomeEntity);
    }

    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser() {
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> ans=incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return ans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteIncome(Long id) {
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity incomeEntity=incomeRepository.findById(id).get();
        if(!incomeEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Uauthorized user");
        }
        incomeRepository.delete(incomeEntity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> ans=incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return ans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal ans=incomeRepository.findAmountByProfileId(profile.getId());
        return ans != null ? ans : BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String Keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> ans=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,Keyword,sort);
        return ans.stream().map(this::toDTO).toList();
    }
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .amount(incomeDTO.getAmount())
                .category(category)
                .profile(profile)
                .icon(incomeDTO.getIcon())
                .name(incomeDTO.getName())
                .build();
    }
    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .amount(incomeEntity.getAmount())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .date(incomeEntity.getDate())
                .category_id(incomeEntity.getCategory().getId())
                .categoryName(incomeEntity.getCategory().getName())
                .profile_id(incomeEntity.getProfile().getId())
                .createAt(incomeEntity.getCreateAt())
                .updateAt(incomeEntity.getUpdateAt())
                .build();
    }
}
