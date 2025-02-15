package com.webauthn.demo.Dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RegistrationRequest {

    private String id;
    private String rawId;
    private String type;
    private String authenticatorAttachment;
    private ResponseData response;
    private Map<String, Object> clientExtensionResults;

    @Data
    public static class ResponseData {
        private String attestationObject;
        private String clientDataJSON;
        private List<String> transports;
        private int publicKeyAlgorithm;
        private String publicKey;
        private String authenticatorData;
    }

}
