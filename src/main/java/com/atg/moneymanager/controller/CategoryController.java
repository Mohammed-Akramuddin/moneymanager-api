package com.atg.moneymanager.controller;

import com.atg.moneymanager.dto.CategoryDTO;
import com.atg.moneymanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CategoryDTO> save(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO cdto=categoryService.saveCategory(categoryDTO);
        return ResponseEntity.ok().body(cdto);
    }
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll(){
        List<CategoryDTO>ans=categoryService.getCategories();
        return ResponseEntity.ok().body(ans);
    }
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> findByType(@PathVariable String type){
        List<CategoryDTO>ans=categoryService.getCategoriesByType(type);
        return ResponseEntity.ok().body(ans);
    }

    @PutMapping("/{cid}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long cid,@RequestBody CategoryDTO categoryDTO){
        CategoryDTO ans=categoryService.updateCategory(cid,categoryDTO);
        return ResponseEntity.ok().body(ans);
    }
}
