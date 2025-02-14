package com.webauthn.demo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String username;
    private String userVerification;
    private String attestation;
    private String attachment;
    private List<String> algorithms;
    private String discoverableCredential;
}
