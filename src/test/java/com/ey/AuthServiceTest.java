package com.ey;

import com.ey.config.JwtService;
import com.ey.dto.*;
import com.ey.entity.Role;
import com.ey.entity.User;
import com.ey.repository.UserRepository;
import com.ey.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private JwtService jwt;

    private PasswordEncoder encoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        encoder = new BCryptPasswordEncoder();

        authService = new AuthService(repo, encoder, jwt);
    }


    @Test
    void register_success() {

        RegisterRequest req = new RegisterRequest();
        req.setName("Hari");
        req.setEmail("hari@gmail.com");
        req.setPassword("Password@123");

        when(repo.existsByEmail("hari@gmail.com")).thenReturn(false);
        when(jwt.generateToken("hari@gmail.com")).thenReturn("dummy-token");

        AuthResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("Authentication successful", res.getMessage());
        assertEquals("dummy-token", res.getAccessToken());
        assertEquals("Hari", res.getName());
        assertTrue(res.getRoles().contains("PATIENT"));

        verify(repo, times(1)).save(any(User.class));
        verify(jwt, times(1)).generateToken("hari@gmail.com");
    }

    @Test
    void register_emailAlreadyExists_shouldFail() {

        RegisterRequest req = new RegisterRequest();
        req.setEmail("hari@gmail.com");

        when(repo.existsByEmail("hari@gmail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(req));

        assertEquals("Email already registered", ex.getMessage());
        verify(repo, never()).save(any(User.class));
    }

    
    @Test
    void login_success() {

        LoginRequest req = new LoginRequest();
        req.setEmail("hari@gmail.com");
        req.setPassword("Password@123");

        User u = new User();
        u.setId(1L);
        u.setName("Hari");
        u.setEmail("hari@gmail.com");
        u.setPassword(encoder.encode("Password@123"));
        u.setRoles(Set.of(Role.PATIENT));

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));
        when(jwt.generateToken("hari@gmail.com")).thenReturn("dummy-token");

        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("dummy-token", res.getAccessToken());
        assertEquals("Hari", res.getName());
        assertTrue(res.getRoles().contains("PATIENT"));

        verify(repo, times(1)).findByEmail("hari@gmail.com");
        verify(jwt, times(1)).generateToken("hari@gmail.com");
    }

    @Test
    void login_wrongPassword_shouldFail() {

        LoginRequest req = new LoginRequest();
        req.setEmail("hari@gmail.com");
        req.setPassword("wrongPass");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setPassword(encoder.encode("Password@123"));

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(req));

        assertEquals("Invalid email/password", ex.getMessage());
    }

    @Test
    void login_userNotFound_shouldFail() {

        LoginRequest req = new LoginRequest();
        req.setEmail("hari@gmail.com");
        req.setPassword("Password@123");

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(req));

        assertEquals("Invalid email/password", ex.getMessage());
    }

   
    @Test
    void forgotPasswordOtp_userNotFound_shouldReturnGenericMessage() {

        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("unknown@gmail.com");

        when(repo.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        String msg = authService.forgotPasswordOtp(req);

        assertEquals("If the account exists, a reset OTP will be sent.", msg);
        verify(repo, never()).save(any(User.class));
    }

    @Test
    void forgotPasswordOtp_success_shouldSetOtpAndExpiry() {

        ForgotPasswordRequest req = new ForgotPasswordRequest();
        req.setEmail("hari@gmail.com");

        User u = new User();
        u.setEmail("hari@gmail.com");

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        String msg = authService.forgotPasswordOtp(req);

        assertEquals("If the account exists, a reset OTP will be sent.", msg);

        assertNotNull(u.getResetOtp());
        assertNotNull(u.getResetOtpExpiry());
        assertEquals(0, u.getResetOtpAttempts());

        verify(repo, times(1)).save(u);
    }

  

    @Test
    void resetPasswordWithOtp_success_validOtp() {

        ResetPasswordOtpRequest req = new ResetPasswordOtpRequest();
        req.setEmail("hari@gmail.com");
        req.setOtp("123456");
        req.setNewPassword("NewPassword@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setResetOtp("123456");
        u.setResetOtpExpiry(OffsetDateTime.now().plusMinutes(5));
        u.setResetOtpAttempts(0);

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        String msg = authService.resetPasswordWithOtp(req);

        assertEquals("Password reset successful", msg);

        
        assertNull(u.getResetOtp());
        assertNull(u.getResetOtpExpiry());
        assertEquals(0, u.getResetOtpAttempts());

       
        assertTrue(encoder.matches("NewPassword@123", u.getPassword()));

        verify(repo, times(1)).save(u);
    }

    @Test
    void resetPasswordWithOtp_otpNotRequested_shouldFail() {

        ResetPasswordOtpRequest req = new ResetPasswordOtpRequest();
        req.setEmail("hari@gmail.com");
        req.setOtp("123456");
        req.setNewPassword("NewPassword@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setResetOtp(null);
        u.setResetOtpExpiry(null);

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPasswordWithOtp(req));

        assertEquals("OTP not requested", ex.getMessage());
    }

    @Test
    void resetPasswordWithOtp_expiredOtp_shouldFail() {

        ResetPasswordOtpRequest req = new ResetPasswordOtpRequest();
        req.setEmail("hari@gmail.com");
        req.setOtp("123456");
        req.setNewPassword("NewPassword@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setResetOtp("123456");
        u.setResetOtpExpiry(OffsetDateTime.now().minusMinutes(1));

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPasswordWithOtp(req));

        assertEquals("OTP expired. Please request again.", ex.getMessage());
    }

    @Test
    void resetPasswordWithOtp_wrongOtp_shouldIncreaseAttemptsAndFail() {

        ResetPasswordOtpRequest req = new ResetPasswordOtpRequest();
        req.setEmail("hari@gmail.com");
        req.setOtp("999999");
        req.setNewPassword("NewPassword@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setResetOtp("123456");
        u.setResetOtpExpiry(OffsetDateTime.now().plusMinutes(5));
        u.setResetOtpAttempts(0);

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPasswordWithOtp(req));

        assertEquals("Invalid OTP", ex.getMessage());
        assertEquals(1, u.getResetOtpAttempts());

        verify(repo, times(1)).save(u);
    }

    @Test
    void resetPasswordWithOtp_attemptsExceeded_shouldFail() {

        ResetPasswordOtpRequest req = new ResetPasswordOtpRequest();
        req.setEmail("hari@gmail.com");
        req.setOtp("123456");
        req.setNewPassword("NewPassword@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setResetOtp("123456");
        u.setResetOtpExpiry(OffsetDateTime.now().plusMinutes(5));
        u.setResetOtpAttempts(5);

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.resetPasswordWithOtp(req));

        assertEquals("Too many wrong attempts. OTP locked.", ex.getMessage());
    }

    @Test
    void changePassword_success() {

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("OldPass@123");
        req.setNewPassword("NewPass@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setPassword(encoder.encode("OldPass@123"));

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        String msg = authService.changePassword("hari@gmail.com", req);

        assertEquals("Password changed successfully", msg);
        assertTrue(encoder.matches("NewPass@123", u.getPassword()));

        verify(repo, times(1)).save(u);
    }

    @Test
    void changePassword_wrongCurrentPassword_shouldFail() {

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrongOld");
        req.setNewPassword("NewPass@123");

        User u = new User();
        u.setEmail("hari@gmail.com");
        u.setPassword(encoder.encode("OldPass@123"));

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.of(u));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.changePassword("hari@gmail.com", req));

        assertEquals("Current password incorrect", ex.getMessage());

        verify(repo, never()).save(any(User.class));
    }

    @Test
    void changePassword_userNotFound_shouldFail() {

        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("OldPass@123");
        req.setNewPassword("NewPass@123");

        when(repo.findByEmail("hari@gmail.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.changePassword("hari@gmail.com", req));

        assertEquals("User not found", ex.getMessage());
    }
}