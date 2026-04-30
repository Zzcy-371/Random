package com.random.app.service;

import com.random.app.dto.OptionCreateDTO;
import com.random.app.dto.OptionUpdateDTO;
import com.random.app.vo.OptionVO;

import java.util.List;

public interface OptionService {
    List<OptionVO> listOptions(Long userId, Long categoryId);
    OptionVO getOption(Long userId, Long optionId);
    OptionVO createOption(Long userId, Long categoryId, OptionCreateDTO dto);
    OptionVO updateOption(Long userId, Long optionId, OptionUpdateDTO dto);
    void deleteOption(Long userId, Long optionId);
    List<OptionVO> batchCreateOptions(Long userId, Long categoryId, List<OptionCreateDTO> dtos);
}
