package com.gateway.walletcentral.modules.auth.controller;

import com.gateway.walletcentral.config.SecurityConfig;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.auth.dto.AuthResponse;
import com.gateway.walletcentral.modules.auth.dto.SendOtpRequest;
import com.gateway.walletcentral.modules.auth.dto.UserInfoResponse;
import com.gateway.walletcentral.modules.auth.dto.VerifyOtpRequest;
import com.gateway.walletcentral.modules.auth.service.AuthService;
import com.gateway.walletcentral.modules.auth.service.RbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Admin OTP authentication operations")
public class AuthController {

    private final AuthService authService;
    private final RbacService rbacService;

    public AuthController(AuthService authService, RbacService rbacService) {
        this.authService = authService;
        this.rbacService = rbacService;
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Send OTP code to admin email")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        authService.sendOtp(request.getEmail());
        Map<String, String> data = Map.of(
                "email", request.getEmail(),
                "message", "OTP code has been sent to your email"
        );
        return ResponseEntity.ok(ApiResponse.ok(data, "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP code and get admin session token")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout admin - revoke session token")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            @RequestHeader("X-API-Key") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
    }

    @PostMapping("/cleanup")
    @Operation(summary = "Manual cleanup of expired OTPs and tokens (users are preserved)")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> cleanup() {
        int otps = authService.cleanupExpiredOtps();
        int tokens = authService.cleanupExpiredTokens();
        Map<String, Integer> data = Map.of("deletedOtps", otps, "deletedTokens", tokens);
        return ResponseEntity.ok(ApiResponse.ok(data, "Cleanup completed"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info with role and permissions")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(
            HttpServletRequest request) {
        String email = (String) request.getAttribute(SecurityConfig.REQUEST_ATTR_EMAIL);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        UserInfoResponse response = rbacService.getUserInfoByEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
