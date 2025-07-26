package com.atg.moneymanager.service;

import com.atg.moneymanager.Model.CategoryEntity;
import com.atg.moneymanager.Model.ProfileEntity;
import com.atg.moneymanager.dto.CategoryDTO;
import com.atg.moneymanager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProfileService profileService;


    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile=profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),profile.getId())){
            throw new RuntimeException("Category name already exists");
        }
        CategoryEntity categoryEntity=toEntity(categoryDTO,profile);
        categoryEntity=categoryRepository.save(categoryEntity);
        return toDTO(categoryEntity);

    }

    public CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .profile(profile)
                .type(categoryDTO.getType())
                .icon(categoryDTO.getIcon() != null ? categoryDTO.getIcon() : "default-icon")
                .build();
    }

    public CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .id(categoryEntity.getId())
                .type(categoryEntity.getType())
                .profile_id(categoryEntity.getProfile()!=null?categoryEntity.getProfile().getId():null)
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }
    public List<CategoryDTO> getCategories() {
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities=categoryRepository.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<CategoryDTO> getCategoriesByType(String type) {
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>categoryEntities=categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return categoryEntities.stream().map(this::toDTO).collect(Collectors.toList());
    }
    public CategoryDTO updateCategory(long id,CategoryDTO categoryDTO) {
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity categoryEntity=categoryRepository.findByIdAndProfileId(id,profile.getId())
                .orElseThrow(() -> new RuntimeException("category not found"));
        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setIcon(categoryDTO.getIcon());
        categoryRepository.save(categoryEntity);
        return toDTO(categoryEntity);
    }
}
