package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.AuthController;
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    Authentication authentication;

    @InjectMocks
    AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    // ---------------------------------------------
    // SIGNIN TEST
    // ---------------------------------------------
    @Test
    void testAuthenticateUserSuccess() {
        LoginRequest login = new LoginRequest();
        login.setEmail("test@gmail.com");
        login.setPassword("secret");

        UserDetailsImpl userDetails =
                new UserDetailsImpl(1L, "testuser", "test@gmail.com", "encodedpass",
                        List.of(() -> "ROLE_USER"));

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mock-jwt");

        ResponseEntity<?> response = authController.authenticateUser(login);

        assertNotNull(response);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("mock-jwt", jwtResponse.getAccessToken());
        assertEquals("test@gmail.com", jwtResponse.getEmail());
    }


    // ---------------------------------------------
    // SIGNUP TESTS
    // ---------------------------------------------
    @Test
    void testSignupUsernameExists() {
        SignupRequest req = new SignupRequest();
        req.setUsername("testuser");
        req.setEmail("test@gmail.com");
        req.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(req);

        MessageResponse body = (MessageResponse) response.getBody();
        assertEquals("Error: Username is already taken!", body.getMessage());
    }

    @Test
    void testSignupEmailExists() {
        SignupRequest req = new SignupRequest();
        req.setUsername("testuser");
        req.setEmail("test@gmail.com");
        req.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(req);

        MessageResponse body = (MessageResponse) response.getBody();
        assertEquals("Error: Email is already in use!", body.getMessage());
    }

    @Test
    void testSignupAssignDefaultUserRole() {
        SignupRequest req = new SignupRequest();
        req.setUsername("testuser");
        req.setEmail("test@gmail.com");
        req.setPassword("password");

        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName(ERole.ROLE_USER);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER))
                .thenReturn(Optional.of(userRole));
        when(encoder.encode("password")).thenReturn("encodedpass");

        ResponseEntity<?> response = authController.registerUser(req);

        MessageResponse body = (MessageResponse) response.getBody();
        assertEquals("User registered successfully!", body.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignupAssignAdminRole() {
        SignupRequest req = new SignupRequest();
        req.setUsername("admin");
        req.setEmail("admin@gmail.com");
        req.setPassword("password");
        req.setRole(Set.of("admin"));

        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(ERole.ROLE_ADMIN);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        when(roleRepository.findByName(ERole.ROLE_ADMIN))
                .thenReturn(Optional.of(adminRole));

        when(encoder.encode("password")).thenReturn("encodedpass");

        ResponseEntity<?> response = authController.registerUser(req);

        MessageResponse body = (MessageResponse) response.getBody();
        assertEquals("User registered successfully!", body.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
