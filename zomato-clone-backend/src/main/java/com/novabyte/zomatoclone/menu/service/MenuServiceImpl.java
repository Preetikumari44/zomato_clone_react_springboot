package com.novabyte.zomatoclone.menu.service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.common.exception.BadRequestException;
import com.novabyte.zomatoclone.common.exception.ForbiddenOperationException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.menu.dto.CategoryDto;
import com.novabyte.zomatoclone.menu.dto.CategoryRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemRequest;
import com.novabyte.zomatoclone.menu.dto.MenuItemResponse;
import com.novabyte.zomatoclone.menu.entity.Category;
import com.novabyte.zomatoclone.menu.entity.MenuItem;
import com.novabyte.zomatoclone.menu.repository.CategoryRepository;
import com.novabyte.zomatoclone.menu.repository.MenuItemRepository;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.restaurant.repository.RestaurantRepository;
import com.novabyte.zomatoclone.upload.service.CloudinaryService;

@Service
public class MenuServiceImpl implements MenuService {

    private static final String MENU_ITEM_FOLDER = "zomato-clone/menu-items";

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final CloudinaryService cloudinaryService;

    public MenuServiceImpl(RestaurantRepository restaurantRepository,
                            CategoryRepository categoryRepository,
                            MenuItemRepository menuItemRepository,
                            CloudinaryService cloudinaryService) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(Long ownerId, Long restaurantId, CategoryRequest request) {
        Restaurant restaurant = findOwnedRestaurant(ownerId, restaurantId);

        Category category = Category.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .build();

        return toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> listCategories(Long restaurantId) {
        findApprovedRestaurant(restaurantId); // 404s if the restaurant isn't public yet
        return categoryRepository.findByRestaurantId(restaurantId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(Long ownerId, Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = findOwnedRestaurant(ownerId, restaurantId);
        Category category = resolveCategory(restaurantId, request.getCategoryId());

        MenuItem item = MenuItem.builder()
                .restaurant(restaurant)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .veg(request.getVeg())
                .available(request.getAvailable())
                .build();

        return toDto(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long ownerId, Long restaurantId, Long itemId, MenuItemRequest request) {
        findOwnedRestaurant(ownerId, restaurantId); // ownership check
        MenuItem item = findItem(restaurantId, itemId);
        Category category = resolveCategory(restaurantId, request.getCategoryId());

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(category);
        item.setVeg(request.getVeg());
        item.setAvailable(request.getAvailable());

        return toDto(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long ownerId, Long restaurantId, Long itemId) {
        findOwnedRestaurant(ownerId, restaurantId);
        MenuItem item = findItem(restaurantId, itemId);
        menuItemRepository.delete(item);
    }

    @Override
    @Transactional
    public MenuItemResponse uploadItemImage(Long ownerId, Long restaurantId, Long itemId, MultipartFile file) {
        findOwnedRestaurant(ownerId, restaurantId);
        MenuItem item = findItem(restaurantId, itemId);
        String url = cloudinaryService.uploadImage(file, MENU_ITEM_FOLDER);
        item.setImageUrl(url);
        return toDto(menuItemRepository.save(item));
    }

    @Override
    public PagedResponse<MenuItemResponse> searchMenu(Long restaurantId, Long categoryId, String keyword,
                                                        Boolean veg, Pageable pageable) {
        findApprovedRestaurant(restaurantId);
        Page<MenuItem> page = menuItemRepository.search(restaurantId, categoryId, keyword, veg, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    // ---- shared helpers ----

    private Restaurant findOwnedRestaurant(Long ownerId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", restaurantId));
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("You do not own this restaurant");
        }
        return restaurant;
    }

    private Restaurant findApprovedRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .filter(r -> r.getStatus() == RestaurantStatus.APPROVED)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", restaurantId));
    }

    private MenuItem findItem(Long restaurantId, Long itemId) {
        return menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> ResourceNotFoundException.of("Menu item", itemId));
    }

    /** categoryId is optional; when present it must belong to the same restaurant. */
    private Category resolveCategory(Long restaurantId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new BadRequestException("Category does not belong to this restaurant"));
    }

    private CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .restaurantId(c.getRestaurant().getId())
                .name(c.getName())
                .build();
    }

    private MenuItemResponse toDto(MenuItem m) {
        return MenuItemResponse.builder()
                .id(m.getId())
                .restaurantId(m.getRestaurant().getId())
                .categoryId(m.getCategory() != null ? m.getCategory().getId() : null)
                .categoryName(m.getCategory() != null ? m.getCategory().getName() : null)
                .name(m.getName())
                .description(m.getDescription())
                .price(m.getPrice())
                .imageUrl(m.getImageUrl())
                .veg(m.isVeg())
                .available(m.isAvailable())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
