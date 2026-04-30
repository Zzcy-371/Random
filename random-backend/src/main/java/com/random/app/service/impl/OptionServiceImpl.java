package com.random.app.service.impl;

import com.random.app.dto.OptionCreateDTO;
import com.random.app.dto.OptionUpdateDTO;
import com.random.app.entity.Category;
import com.random.app.entity.Option;
import com.random.app.entity.User;
import com.random.app.exception.BusinessException;
import com.random.app.repository.CategoryRepository;
import com.random.app.repository.OptionRepository;
import com.random.app.repository.UserRepository;
import com.random.app.service.OptionService;
import com.random.app.vo.OptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<OptionVO> listOptions(Long userId, Long categoryId) {
        return optionRepository.findByUserIdAndCategoryIdAndIsActiveTrue(userId, categoryId)
                .stream().map(this::toVO).toList();
    }

    @Override
    public OptionVO getOption(Long userId, Long optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException("选项不存在"));
        if (!option.getUser().getId().equals(userId)) {
            throw new BusinessException(403, "无权访问该选项");
        }
        return toVO(option);
    }

    @Override
    public OptionVO createOption(Long userId, Long categoryId, OptionCreateDTO dto) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        Option option = new Option();
        option.setUser(user);
        option.setCategory(category);
        option.setName(dto.getName());
        option.setDescription(dto.getDescription());
        option.setTags(dto.getTags());
        option.setImageUrl(dto.getImageUrl());
        optionRepository.save(option);
        return toVO(option);
    }

    @Override
    public OptionVO updateOption(Long userId, Long optionId, OptionUpdateDTO dto) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException("选项不存在"));
        if (!option.getUser().getId().equals(userId)) {
            throw new BusinessException(403, "无权修改该选项");
        }
        if (dto.getName() != null) option.setName(dto.getName());
        if (dto.getDescription() != null) option.setDescription(dto.getDescription());
        if (dto.getTags() != null) option.setTags(dto.getTags());
        if (dto.getImageUrl() != null) option.setImageUrl(dto.getImageUrl());
        optionRepository.save(option);
        return toVO(option);
    }

    @Override
    public void deleteOption(Long userId, Long optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException("选项不存在"));
        if (!option.getUser().getId().equals(userId)) {
            throw new BusinessException(403, "无权删除该选项");
        }
        option.setIsActive(false);
        optionRepository.save(option);
    }

    @Override
    public List<OptionVO> batchCreateOptions(Long userId, Long categoryId, List<OptionCreateDTO> dtos) {
        return dtos.stream()
                .map(dto -> createOption(userId, categoryId, dto))
                .toList();
    }

    private OptionVO toVO(Option option) {
        return OptionVO.builder()
                .id(option.getId())
                .categoryId(option.getCategory().getId())
                .categoryName(option.getCategory().getDisplayName())
                .name(option.getName())
                .description(option.getDescription())
                .tags(option.getTagList())
                .imageUrl(option.getImageUrl())
                .createdAt(option.getCreatedAt())
                .build();
    }
}
