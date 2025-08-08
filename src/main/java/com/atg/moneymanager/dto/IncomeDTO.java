package com.atg.moneymanager.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeDTO {
    private Long id;
    private String name;
    private String categoryName;
    private BigDecimal amount;
    private String icon;
    private LocalDate date;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long profile_id;
    private Long category_id;
}
