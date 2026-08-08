package com.novabyte.zomatoclone.restaurant.service;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.common.exception.ForbiddenOperationException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantRequest;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.restaurant.repository.RestaurantRepository;
import com.novabyte.zomatoclone.upload.service.CloudinaryService;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private static final String LOGO_FOLDER = "zomato-clone/restaurants";

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                  UserRepository userRepository,
                                  CloudinaryService cloudinaryService) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public RestaurantResponse create(Long ownerId, RestaurantRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", ownerId));

        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.getName())
                .description(request.getDescription())
                .cuisineType(request.getCuisineType())
                .address(request.getAddress())
                .city(request.getCity())
                .status(RestaurantStatus.PENDING)
                .build();

        return toDto(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional
    public RestaurantResponse update(Long ownerId, Long restaurantId, RestaurantRequest request) {
        Restaurant restaurant = findOwned(ownerId, restaurantId);

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());

        // Editing a previously rejected restaurant sends it back into the
        // approval queue rather than leaving it silently REJECTED forever.
        if (restaurant.getStatus() == RestaurantStatus.REJECTED) {
            restaurant.setStatus(RestaurantStatus.PENDING);
            restaurant.setRejectionReason(null);
        }

        return toDto(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional
    public RestaurantResponse uploadLogo(Long ownerId, Long restaurantId, MultipartFile file) {
        Restaurant restaurant = findOwned(ownerId, restaurantId);
        String url = cloudinaryService.uploadImage(file, LOGO_FOLDER);
        restaurant.setLogoUrl(url);
        return toDto(restaurantRepository.save(restaurant));
    }

    @Override
    public RestaurantResponse getApprovedById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .filter(r -> r.getStatus() == RestaurantStatus.APPROVED)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", id));
        return toDto(restaurant);
    }

    @Override
    public PagedResponse<RestaurantResponse> searchApproved(String keyword, String city, String cuisineType, Pageable pageable) {
        String kw = StringUtils.hasText(keyword) ? keyword : null;
        String cty = StringUtils.hasText(city) ? city : null;
        String cuisine = StringUtils.hasText(cuisineType) ? cuisineType : null;

        Page<Restaurant> page = restaurantRepository.searchApproved(kw, cty, cuisine, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    public PagedResponse<RestaurantResponse> listMine(Long ownerId, Pageable pageable) {
        Page<Restaurant> page = restaurantRepository.findByOwnerId(ownerId, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    @Transactional
    public RestaurantResponse approve(Long restaurantId) {
        Restaurant restaurant = findById(restaurantId);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        restaurant.setRejectionReason(null);
        return toDto(restaurantRepository.save(restaurant));
    }

    @Override
    @Transactional
    public RestaurantResponse reject(Long restaurantId, String reason) {
        Restaurant restaurant = findById(restaurantId);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurant.setRejectionReason(reason);
        return toDto(restaurantRepository.save(restaurant));
    }

    private Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", id));
    }

    /** Loads a restaurant and enforces that `ownerId` is actually its owner. */
    private Restaurant findOwned(Long ownerId, Long restaurantId) {
        Restaurant restaurant = findById(restaurantId);
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("You do not own this restaurant");
        }
        return restaurant;
    }

    private RestaurantResponse toDto(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .ownerId(r.getOwner().getId())
                .ownerName(r.getOwner().getFullName())
                .name(r.getName())
                .description(r.getDescription())
                .cuisineType(r.getCuisineType())
                .address(r.getAddress())
                .city(r.getCity())
                .logoUrl(r.getLogoUrl())
                .rejectionReason(r.getRejectionReason())
                .status(r.getStatus())
                .avgRating(r.getAvgRating())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
