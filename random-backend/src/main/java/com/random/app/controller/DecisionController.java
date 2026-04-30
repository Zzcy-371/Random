package com.random.app.controller;

import com.random.app.dto.DecideRequestDTO;
import com.random.app.service.DecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping("/categories/{catId}/decide")
    public Map<String, Object> decide(Authentication auth, @PathVariable Long catId,
                                      @RequestBody(required = false) DecideRequestDTO dto) {
        Long userId = (Long) auth.getPrincipal();
        if (dto == null) dto = new DecideRequestDTO();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "决策完成");
        result.put("data", decisionService.decide(userId, catId, dto));
        return result;
    }

    @GetMapping("/decisions")
    public Map<String, Object> getHistory(Authentication auth,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", decisionService.getHistory(userId, PageRequest.of(page, size)));
        return result;
    }

    @GetMapping("/decisions/{id}")
    public Map<String, Object> getDecision(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", decisionService.getDecision(userId, id));
        return result;
    }
}
