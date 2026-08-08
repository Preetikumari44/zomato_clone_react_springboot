package com.novabyte.zomatoclone.user.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.common.exception.BadRequestException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.security.JwtTokenProvider;
import com.novabyte.zomatoclone.security.UserPrincipal;
import com.novabyte.zomatoclone.user.dto.AuthResponse;
import com.novabyte.zomatoclone.user.dto.LoginRequest;
import com.novabyte.zomatoclone.user.dto.RegisterRequest;
import com.novabyte.zomatoclone.user.dto.SwitchRoleRequest;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.entity.UserRole;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }

        // ADMIN can never be self-assigned through public registration —
        // admins are seeded or created by an existing admin (Phase 7).
        Set<Role> requestedRoles = request.getRoles().stream()
                .filter(role -> role != Role.ADMIN)
                .collect(Collectors.toSet());

        if (requestedRoles.isEmpty()) {
            throw new BadRequestException("At least one non-admin role must be selected");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        requestedRoles.forEach(user::addRole);

        User saved = userRepository.save(user);

        Role activeRole = defaultActiveRole(requestedRoles);
        String token = jwtTokenProvider.generateToken(saved.getId(), saved.getEmail(), activeRole, requestedRoles);

        return AuthResponse.builder()
                .token(token)
                .userId(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .activeRole(activeRole)
                .availableRoles(requestedRoles)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Delegates to CustomUserDetailsService + BCrypt comparison; throws
        // BadCredentialsException (handled by GlobalExceptionHandler) on mismatch.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found for this email"));

        Set<Role> roles = user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet());
        Role activeRole = defaultActiveRole(roles);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), activeRole, roles);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .activeRole(activeRole)
                .availableRoles(roles)
                .build();
    }

    @Override
    public AuthResponse switchRole(UserPrincipal principal, SwitchRoleRequest request) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResourceNotFoundException("No account found"));

        Set<Role> roles = user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet());

        if (!roles.contains(request.getRole())) {
            throw new BadRequestException("This account does not hold the " + request.getRole() + " role");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), request.getRole(), roles);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .activeRole(request.getRole())
                .availableRoles(roles)
                .build();
    }

    /** CUSTOMER is the friendliest default landing role when a user holds several. */
    private Role defaultActiveRole(Set<Role> roles) {
        if (roles.contains(Role.CUSTOMER)) {
            return Role.CUSTOMER;
        }
        return roles.iterator().next();
    }
}
