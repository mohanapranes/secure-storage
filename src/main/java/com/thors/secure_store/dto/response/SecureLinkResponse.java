package com.thors.secure_store.dto.response;

import com.thors.secure_store.model.AccessRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SecureLinkResponse {
    private int statusCode;
    private String shareLink; // e.g. https://domain.com/api/share/{shareId}
    private String otp;       // plain OTP to be sent securely to user
    private Instant expiresAt;
    private AccessRole accessRole;
    private String message;
}
