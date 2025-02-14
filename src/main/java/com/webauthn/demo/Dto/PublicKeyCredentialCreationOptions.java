package com.webauthn.demo.Dto;

import lombok.Data;

import java.util.List;

@Data
public class PublicKeyCredentialCreationOptions {
    private Rp rp;
    private UserDto user;
    private String challenge;
    private List<PublicKeyCredParams> pubKeyCredParams;
    private Long timeout;
    private String attestation;
    private AuthenticatorSelection authenticatorSelection;

    @Data
    public class Rp {
        private String name;
        private String id;
    }

    @Data
    public class PublicKeyCredParams {
        private String type;
        private String alg;
    }

    @Data
    public class AuthenticatorSelection {
        private String authenticatorAttachment;
        private Boolean requireResidentKey;
        private String userVerification;
        private String residentKey;
    }
}
