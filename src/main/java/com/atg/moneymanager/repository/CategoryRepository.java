package com.atg.moneymanager.repository;

import com.atg.moneymanager.Model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    List<CategoryEntity> findByProfileId(Long profile_id);

    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profile_id);

    List<CategoryEntity>findByTypeAndProfileId(String type,Long profile_id);

    Boolean existsByNameAndProfileId(String name,Long profile_id);

}
