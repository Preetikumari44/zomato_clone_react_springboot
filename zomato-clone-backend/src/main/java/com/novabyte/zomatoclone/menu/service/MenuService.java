package com.novabyte.zomatoclone.menu.service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.menu.dto.CategoryDto;
import com.novabyte.zomatoclone.menu.dto.CategoryRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemResponse;

public interface MenuService {

    CategoryDto createCategory(Long ownerId, Long restaurantId, CategoryRequest request);

    List<CategoryDto> listCategories(Long restaurantId);

    MenuItemResponse createMenuItem(Long ownerId, Long restaurantId, MenuItemRequest request);

    MenuItemResponse updateMenuItem(Long ownerId, Long restaurantId, Long itemId, MenuItemRequest request);

    void deleteMenuItem(Long ownerId, Long restaurantId, Long itemId);

    MenuItemResponse uploadItemImage(Long ownerId, Long restaurantId, Long itemId, MultipartFile file);

    PagedResponse<MenuItemResponse> searchMenu(Long restaurantId, Long categoryId, String keyword, Boolean veg, Pageable pageable);
}
