package com.webauthn.demo.Dto;

import lombok.Data;

@Data
public class AuthenticatorDto {
    private Long id;
    private String name;
    private String credentialId;
    private String publicKey;
    private String signCount;
    private String userId;
}
