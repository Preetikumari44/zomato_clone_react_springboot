package com.novabyte.zomatoclone.cart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.novabyte.zomatoclone.cart.dto.AddToCartRequest;
import com.novabyte.zomatoclone.cart.dto.CartItemResponse;
import com.novabyte.zomatoclone.cart.dto.CartResponse;
import com.novabyte.zomatoclone.cart.dto.UpdateCartItemRequest;
import com.novabyte.zomatoclone.cart.entity.Cart;
import com.novabyte.zomatoclone.cart.entity.CartItem;
import com.novabyte.zomatoclone.cart.repository.CartItemRepository;
import com.novabyte.zomatoclone.cart.repository.CartRepository;
import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.common.exception.BadRequestException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.menu.entity.MenuItem;
import com.novabyte.zomatoclone.menu.repository.MenuItemRepository;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            MenuItemRepository menuItemRepository,
                            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CartResponse getMyCart(Long customerId) {
        return toDto(findOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public CartResponse addItem(Long customerId, AddToCartRequest request) {
        Cart cart = findOrCreateCart(customerId);
        MenuItem menuItem = findPurchasableMenuItem(request.getMenuItemId());
        Restaurant itemRestaurant = menuItem.getRestaurant();

        boolean cartHasDifferentRestaurant = cart.getRestaurant() != null
                && !cart.getRestaurant().getId().equals(itemRestaurant.getId());

        if (cartHasDifferentRestaurant) {
            if (!request.isReplaceCart()) {
                throw new BadRequestException(
                        "Your cart has items from " + cart.getRestaurant().getName() +
                        ". Pass replaceCart=true to clear it and order from " + itemRestaurant.getName() + " instead.");
            }
            cart.getItems().clear(); // orphanRemoval deletes the old cart_items rows
        }

        cart.setRestaurant(itemRestaurant);

        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(Long customerId, Long menuItemId, UpdateCartItemRequest request) {
        Cart cart = findOrCreateCart(customerId);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("Cart item for menu item", menuItemId));

        item.setQuantity(request.getQuantity());
        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long customerId, Long menuItemId) {
        Cart cart = findOrCreateCart(customerId);
        boolean removed = cart.getItems().removeIf(ci -> ci.getMenuItem().getId().equals(menuItemId));

        if (!removed) {
            throw ResourceNotFoundException.of("Cart item for menu item", menuItemId);
        }
        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null); // free the cart up for a different restaurant next time
        }
        return toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = findOrCreateCart(customerId);
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);
    }

    // ---- helpers ----

    private Cart findOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            User customer = userRepository.findById(customerId)
                    .orElseThrow(() -> ResourceNotFoundException.of("User", customerId));
            Cart cart = Cart.builder().customer(customer).build();
            return cartRepository.save(cart);
        });
    }

    /** Menu item must exist, belong to an APPROVED restaurant, and currently be available. */
    private MenuItem findPurchasableMenuItem(Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> ResourceNotFoundException.of("Menu item", menuItemId));

        if (item.getRestaurant().getStatus() != RestaurantStatus.APPROVED) {
            throw ResourceNotFoundException.of("Menu item", menuItemId);
        }
        if (!item.isAvailable()) {
            throw new BadRequestException("\"" + item.getName() + "\" is currently unavailable");
        }
        return item;
    }

    private CartResponse toDto(Cart cart) {
        List<CartItemResponse> itemDtos = cart.getItems().stream()
                .map(ci -> CartItemResponse.builder()
                        .cartItemId(ci.getId())
                        .menuItemId(ci.getMenuItem().getId())
                        .name(ci.getMenuItem().getName())
                        .price(ci.getMenuItem().getPrice())
                        .imageUrl(ci.getMenuItem().getImageUrl())
                        .veg(ci.getMenuItem().isVeg())
                        .quantity(ci.getQuantity())
                        .subtotal(ci.getMenuItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .toList();

        BigDecimal total = itemDtos.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = itemDtos.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .restaurantId(cart.getRestaurant() != null ? cart.getRestaurant().getId() : null)
                .restaurantName(cart.getRestaurant() != null ? cart.getRestaurant().getName() : null)
                .items(itemDtos)
                .itemCount(itemCount)
                .totalAmount(total)
                .build();
    }
}
