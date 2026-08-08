package com.novabyte.zomatoclone.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.menu.entity.Category;
import com.novabyte.zomatoclone.menu.entity.MenuItem;
import com.novabyte.zomatoclone.menu.repository.CategoryRepository;
import com.novabyte.zomatoclone.menu.repository.MenuItemRepository;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.restaurant.repository.RestaurantRepository;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

/**
 * Seeds one account per role (all password: Password@123) plus one
 * approved restaurant with a menu, so the API is exercisable immediately
 * after a fresh deploy without manual setup. Runs only against an empty
 * `users` table — safe to leave enabled in prod.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       CategoryRepository categoryRepository,
                       MenuItemRepository menuItemRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already seeded
        }

        String password = passwordEncoder.encode("Password@123");

        User admin = save(User.builder().fullName("Ava Admin").email("admin@zomatoclone.dev")
                .passwordHash(password).build(), Role.ADMIN);

        User owner = save(User.builder().fullName("Ravi Owner").email("owner@zomatoclone.dev")
                .passwordHash(password).build(), Role.RESTAURANT_OWNER);

        save(User.builder().fullName("Chloe Customer").email("customer@zomatoclone.dev")
                .passwordHash(password).build(), Role.CUSTOMER);

        save(User.builder().fullName("Dev Delivery").email("delivery@zomatoclone.dev")
                .passwordHash(password).build(), Role.DELIVERY_PARTNER);

        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .owner(owner)
                .name("Spice Route")
                .description("North Indian and Mughlai favorites")
                .cuisineType("Indian")
                .address("12 MG Road")
                .city("Bengaluru")
                .status(RestaurantStatus.APPROVED)
                .build());

        Category mains = categoryRepository.save(Category.builder().restaurant(restaurant).name("Mains").build());
        Category starters = categoryRepository.save(Category.builder().restaurant(restaurant).name("Starters").build());

        menuItemRepository.save(MenuItem.builder().restaurant(restaurant).category(mains)
                .name("Butter Chicken").description("Creamy tomato curry").price(new BigDecimal("320.00"))
                .veg(false).available(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant).category(mains)
                .name("Paneer Tikka Masala").description("Cottage cheese in spiced gravy").price(new BigDecimal("280.00"))
                .veg(true).available(true).build());
        menuItemRepository.save(MenuItem.builder().restaurant(restaurant).category(starters)
                .name("Veg Spring Rolls").description("Crispy rolls with sweet chili dip").price(new BigDecimal("180.00"))
                .veg(true).available(true).build());

        System.out.println("Seed data loaded — admin@zomatoclone.dev / owner@zomatoclone.dev / " +
                "customer@zomatoclone.dev / delivery@zomatoclone.dev, all password: Password@123");
    }

    private User save(User user, Role role) {
        user.addRole(role);
        return userRepository.save(user);
    }
}
