package com.random.app.controller;

import com.random.app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public Map<String, Object> listCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", categoryRepository.findAll());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCategory(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", categoryRepository.findById(id).orElse(null));
        return result;
    }
}
