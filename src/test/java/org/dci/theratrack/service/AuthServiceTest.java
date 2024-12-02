package org.dci.theratrack.service;

import org.dci.theratrack.entity.User;
import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.repository.UserRepository;
import org.dci.theratrack.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

  @InjectMocks
  private AuthService authService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAuthenticateSuccess() {
    // Arrange
    String username = "user";
    String password = "password";
    String encodedPassword = "encodedPassword";
    String userRole = "ADMIN";
    String mockToken = "mockJwtToken";

    User mockUser = new User();
    mockUser.setUsername(username);
    mockUser.setPassword(encodedPassword);
    mockUser.setUserRole(UserRole.ADMIN);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
    when(jwtUtil.generateToken(username, userRole)).thenReturn(mockToken);

    // Act
    String token = authService.authenticate(username, password);

    // Assert
    assertEquals(mockToken, token);
    verify(userRepository, times(1)).findByUsername(username);
    verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    verify(jwtUtil, times(1)).generateToken(username, userRole);
  }

  @Test
  void testAuthenticateUserNotFound() {
    // Arrange
    String username = "nonExistentUser";
    String password = "password";

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(username, password));
    assertEquals("User not found", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
    verifyNoInteractions(passwordEncoder, jwtUtil);
  }

  @Test
  void testAuthenticateInvalidCredentials() {
    // Arrange
    String username = "user";
    String password = "wrongPassword";
    String encodedPassword = "encodedPassword";

    User mockUser = new User();
    mockUser.setUsername(username);
    mockUser.setPassword(encodedPassword);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(username, password));
    assertEquals("Invalid credentials", exception.getMessage());
    verify(userRepository, times(1)).findByUsername(username);
    verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    verifyNoInteractions(jwtUtil);
  }
}

