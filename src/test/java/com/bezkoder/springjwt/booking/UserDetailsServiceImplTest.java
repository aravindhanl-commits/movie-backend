package com.bezkoder.springjwt.booking;


import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByEmailSuccess() {

        User user = new User("john", "john@gmail.com", "pwd123");
        user.setId(1L);

        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        user.setRoles(Set.of(role));

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("john@gmail.com");

       assertEquals("john", result.getUsername());
        assertEquals("pwd123", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserNotFound() {

        when(userRepository.findByEmail("nope@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nope@gmail.com");
        });
    }
}
