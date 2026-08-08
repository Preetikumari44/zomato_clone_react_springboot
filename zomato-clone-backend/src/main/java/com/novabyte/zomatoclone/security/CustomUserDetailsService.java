package com.novabyte.zomatoclone.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

/**
 * Used ONLY by AuthenticationManager during login (AuthServiceImpl#login)
 * to verify the submitted password against the stored hash. It is NOT
 * consulted on every request — that's the JwtAuthenticationFilter's job,
 * working purely off the token's activeRole claim. The authorities
 * returned here (all of the user's roles) are irrelevant post-login.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .authorities(user.getRoles().stream()
                        .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().name()))
                        .collect(Collectors.toSet()))
                .build();
    }
}
