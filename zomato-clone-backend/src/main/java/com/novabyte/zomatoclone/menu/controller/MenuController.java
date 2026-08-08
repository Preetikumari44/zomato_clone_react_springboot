package com.novabyte.zomatoclone.menu.controller;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.menu.dto.CategoryDto;
import com.novabyte.zomatoclone.menu.dto.CategoryRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemResponse;
import com.novabyte.zomatoclone.menu.service.MenuService;
import com.novabyte.zomatoclone.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}")
@Tag(name = "Menu", description = "Categories and menu items, scoped to a restaurant")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryDto>> listCategories(@PathVariable Long restaurantId) {
        return ApiResponse.success(menuService.listCategories(restaurantId));
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryDto> createCategory(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable Long restaurantId,
                                                     @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(menuService.createCategory(principal.userId(), restaurantId, request));
    }

    @GetMapping("/menu")
    public ApiResponse<PagedResponse<MenuItemResponse>> search(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean veg,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(menuService.searchMenu(restaurantId, categoryId, keyword, veg, pageable));
    }

    @PostMapping("/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MenuItemResponse> createItem(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long restaurantId,
                                                      @Valid @RequestBody MenuItemRequest request) {
        return ApiResponse.success(menuService.createMenuItem(principal.userId(), restaurantId, request));
    }

    @PutMapping("/menu/{itemId}")
    public ApiResponse<MenuItemResponse> updateItem(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long restaurantId,
                                                       @PathVariable Long itemId,
                                                       @Valid @RequestBody MenuItemRequest request) {
        return ApiResponse.success("Menu item updated", menuService.updateMenuItem(principal.userId(), restaurantId, itemId, request));
    }

    @DeleteMapping("/menu/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@AuthenticationPrincipal UserPrincipal principal,
                            @PathVariable Long restaurantId,
                            @PathVariable Long itemId) {
        menuService.deleteMenuItem(principal.userId(), restaurantId, itemId);
    }

    @PostMapping(value = "/menu/{itemId}/image", consumes = "multipart/form-data")
    public ApiResponse<MenuItemResponse> uploadItemImage(@AuthenticationPrincipal UserPrincipal principal,
                                                            @PathVariable Long restaurantId,
                                                            @PathVariable Long itemId,
                                                            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Item image uploaded", menuService.uploadItemImage(principal.userId(), restaurantId, itemId, file));
    }
}
