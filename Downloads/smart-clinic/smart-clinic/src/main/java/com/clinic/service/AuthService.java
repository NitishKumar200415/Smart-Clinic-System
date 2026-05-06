package com.clinic.service;

import com.clinic.config.JwtUtil;
import com.clinic.dto.AuthResponse;
import com.clinic.dto.OtpRequest;
import com.clinic.dto.OtpVerifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock OTP service.
 * In production: replace the in-memory store with Redis and
 * integrate an SMS gateway (Twilio, AWS SNS, etc.).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String MOCK_OTP = "123456";

    private final JwtUtil jwtUtil;

    // phone → otp (in-memory; not for production use)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public Map<String, String> sendOtp(OtpRequest request) {
        // Simulate sending OTP
        otpStore.put(request.getPhone(), MOCK_OTP);
        log.info("[MOCK] OTP {} sent to {}", MOCK_OTP, request.getPhone());

        // In dev, we expose the OTP in the response so you can test immediately
        return Map.of(
                "message", "OTP sent successfully (mock)",
                "otp", MOCK_OTP   // Remove this line in production!
        );
    }

    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String stored = otpStore.get(request.getPhone());

        if (stored == null) {
            throw new IllegalArgumentException("No OTP requested for this phone number");
        }
        if (!stored.equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpStore.remove(request.getPhone());
        String token = jwtUtil.generateToken(request.getPhone());

        log.info("OTP verified for {}. JWT issued.", request.getPhone());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .phone(request.getPhone())
                .build();
    }
}
