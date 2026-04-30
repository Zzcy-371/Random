package com.random.app.controller;

import com.random.app.dto.OptionCreateDTO;
import com.random.app.dto.OptionUpdateDTO;
import com.random.app.service.OptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @GetMapping("/categories/{catId}/options")
    public Map<String, Object> listOptions(Authentication auth, @PathVariable Long catId) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", optionService.listOptions(userId, catId));
        return result;
    }

    @PostMapping("/categories/{catId}/options")
    public Map<String, Object> createOption(Authentication auth, @PathVariable Long catId,
                                            @Valid @RequestBody OptionCreateDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "创建成功");
        result.put("data", optionService.createOption(userId, catId, dto));
        return result;
    }

    @PostMapping("/categories/{catId}/options/batch")
    public Map<String, Object> batchCreateOptions(Authentication auth, @PathVariable Long catId,
                                                   @Valid @RequestBody List<OptionCreateDTO> dtos) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "批量创建成功");
        result.put("data", optionService.batchCreateOptions(userId, catId, dtos));
        return result;
    }

    @GetMapping("/options/{id}")
    public Map<String, Object> getOption(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", optionService.getOption(userId, id));
        return result;
    }

    @PutMapping("/options/{id}")
    public Map<String, Object> updateOption(Authentication auth, @PathVariable Long id,
                                            @Valid @RequestBody OptionUpdateDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "更新成功");
        result.put("data", optionService.updateOption(userId, id, dto));
        return result;
    }

    @DeleteMapping("/options/{id}")
    public Map<String, Object> deleteOption(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        optionService.deleteOption(userId, id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "删除成功");
        return result;
    }
}
