package com.novabyte.zomatoclone.order.service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.novabyte.zomatoclone.cart.entity.Cart;
import com.novabyte.zomatoclone.cart.repository.CartRepository;
import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.common.exception.BadRequestException;
import com.novabyte.zomatoclone.common.exception.ForbiddenOperationException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.delivery.repository.DeliveryAssignmentRepository;
import com.novabyte.zomatoclone.order.dto.OrderItemResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.order.dto.OrderStatusHistoryResponse;
import com.novabyte.zomatoclone.order.dto.OrderStatusUpdateRequest;
import com.novabyte.zomatoclone.order.dto.PlaceOrderRequest;
import com.novabyte.zomatoclone.order.entity.Order;
import com.novabyte.zomatoclone.order.entity.OrderItem;
import com.novabyte.zomatoclone.order.entity.OrderStatusHistory;
import com.novabyte.zomatoclone.order.repository.OrderRepository;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.restaurant.repository.RestaurantRepository;
import com.novabyte.zomatoclone.security.UserPrincipal;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                             CartRepository cartRepository,
                             UserRepository userRepository,
                             RestaurantRepository restaurantRepository,
                             DeliveryAssignmentRepository deliveryAssignmentRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.deliveryAssignmentRepository = deliveryAssignmentRepository;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Long customerId, PlaceOrderRequest request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", customerId));

        Order order = Order.builder()
                .customer(customer)
                .restaurant(cart.getRestaurant())
                .deliveryAddress(request.getDeliveryAddress())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PLACED)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (var cartItem : cart.getItems()) {
            BigDecimal linePrice = cartItem.getMenuItem().getPrice();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(cartItem.getMenuItem())
                    .itemNameSnapshot(cartItem.getMenuItem().getName())
                    .priceSnapshot(linePrice)
                    .quantity(cartItem.getQuantity())
                    .build();
            order.getItems().add(orderItem);
            total = total.add(linePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        order.getStatusHistory().add(historyEntry(order, OrderStatus.PLACED, customer));

        Order saved = orderRepository.save(order);

        // Cart is consumed on successful order placement.
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);

        return toDto(saved);
    }

    @Override
    public OrderResponse getOrder(UserPrincipal principal, Long orderId) {
        Order order = findById(orderId);
        authorizeView(principal, order);
        return toDto(order);
    }

    @Override
    public PagedResponse<OrderResponse> listMine(Long customerId, Pageable pageable) {
        Page<Order> page = orderRepository.findByCustomerIdOrderByPlacedAtDesc(customerId, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    public PagedResponse<OrderResponse> listForRestaurant(Long ownerId, Long restaurantId, Pageable pageable) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> ResourceNotFoundException.of("Restaurant", restaurantId));
        assertOwnsRestaurant(ownerId, restaurant);

        Page<Order> page = orderRepository.findByRestaurantIdOrderByPlacedAtDesc(restaurantId, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    @Transactional
    public OrderResponse accept(Long ownerId, Long orderId) {
        Order order = findOwnedByRestaurant(ownerId, orderId);
        requireCurrentStatus(order, OrderStatus.PLACED, OrderStatus.ACCEPTED);
        return transition(order, OrderStatus.ACCEPTED, order.getRestaurant().getOwner());
    }

    @Override
    @Transactional
    public OrderResponse reject(Long ownerId, Long orderId) {
        Order order = findOwnedByRestaurant(ownerId, orderId);
        requireCurrentStatus(order, OrderStatus.PLACED, OrderStatus.REJECTED);
        return transition(order, OrderStatus.REJECTED, order.getRestaurant().getOwner());
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long ownerId, Long orderId, OrderStatusUpdateRequest request) {
        Order order = findOwnedByRestaurant(ownerId, orderId);
        OrderStatus requested = request.getStatus();

        if (requested != OrderStatus.PREPARING && requested != OrderStatus.READY_FOR_PICKUP) {
            throw new BadRequestException(
                    "This endpoint only moves an order to PREPARING or READY_FOR_PICKUP; " +
                    "use /accept or /reject for the initial decision");
        }

        OrderStatus requiredCurrent = requested == OrderStatus.PREPARING ? OrderStatus.ACCEPTED : OrderStatus.PREPARING;
        requireCurrentStatus(order, requiredCurrent, requested);

        return transition(order, requested, order.getRestaurant().getOwner());
    }

    // ---- state machine + access control helpers ----

    /** Enforces that `order` is currently in `expectedCurrent` before allowing a move toward `target`. */
    private void requireCurrentStatus(Order order, OrderStatus expectedCurrent, OrderStatus target) {
        if (order.getStatus() != expectedCurrent) {
            throw new BadRequestException(
                    "Cannot move order from " + order.getStatus() + " to " + target +
                    " (expected current status " + expectedCurrent + ")");
        }
    }

    private OrderResponse transition(Order order, OrderStatus newStatus, User actor) {
        order.setStatus(newStatus);
        order.getStatusHistory().add(historyEntry(order, newStatus, actor));
        return toDto(orderRepository.save(order));
    }

    private OrderStatusHistory historyEntry(Order order, OrderStatus status, User actor) {
        return OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .changedBy(actor)
                .build();
    }

    private void authorizeView(UserPrincipal principal, Order order) {
        boolean isOwningCustomer = principal.activeRole() == Role.CUSTOMER
                && order.getCustomer().getId().equals(principal.userId());
        boolean isOwningRestaurant = principal.activeRole() == Role.RESTAURANT_OWNER
                && order.getRestaurant().getOwner().getId().equals(principal.userId());
        boolean isAdmin = principal.activeRole() == Role.ADMIN;
        boolean isAssignedDeliveryPartner = principal.activeRole() == Role.DELIVERY_PARTNER
                && deliveryAssignmentRepository.findByOrderId(order.getId())
                        .map(a -> a.getDeliveryPartner() != null
                                && a.getDeliveryPartner().getUser().getId().equals(principal.userId()))
                        .orElse(false);

        if (!(isOwningCustomer || isOwningRestaurant || isAdmin || isAssignedDeliveryPartner)) {
            throw new ForbiddenOperationException("You do not have access to this order");
        }
    }

    private void assertOwnsRestaurant(Long ownerId, Order order) {
        assertOwnsRestaurant(ownerId, order.getRestaurant());
    }

    private void assertOwnsRestaurant(Long ownerId, Restaurant restaurant) {
        if (!restaurant.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("You do not own this restaurant");
        }
    }

    private Order findOwnedByRestaurant(Long ownerId, Long orderId) {
        Order order = findById(orderId);
        assertOwnsRestaurant(ownerId, order);
        return order;
    }

    private Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
    }

    private OrderResponse toDto(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .menuItemId(i.getMenuItem().getId())
                        .name(i.getItemNameSnapshot())
                        .price(i.getPriceSnapshot())
                        .quantity(i.getQuantity())
                        .subtotal(i.getPriceSnapshot().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .toList();

        List<OrderStatusHistoryResponse> history = o.getStatusHistory().stream()
                .sorted((a, b) -> a.getChangedAt().compareTo(b.getChangedAt()))
                .map(h -> OrderStatusHistoryResponse.builder()
                        .status(h.getStatus())
                        .changedAt(h.getChangedAt())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(o.getId())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getFullName())
                .restaurantId(o.getRestaurant().getId())
                .restaurantName(o.getRestaurant().getName())
                .deliveryAddress(o.getDeliveryAddress())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .items(items)
                .statusHistory(history)
                .placedAt(o.getPlacedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
