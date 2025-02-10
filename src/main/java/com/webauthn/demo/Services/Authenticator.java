package com.webauthn.demo.Services;

import com.webauthn.demo.Dto.AuthenticatorDto;
import com.webauthn.demo.Dto.UserDto;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.AttestedCredentialData;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;

import java.util.Optional;

public class Authenticator {

    public static AuthenticatorDto fromRegistrationResult(
            RegistrationResult result,
            AuthenticatorAttestationResponse response,
            UserDto user,
            String name) {

        Optional<AttestedCredentialData> attestationData = response.getAttestation()
                .getAuthenticatorData()
                .getAttestedCredentialData();

        AuthenticatorDto dto = new AuthenticatorDto();
        dto.setName(name);
        dto.setCredentialId(result.getKeyId().getId().getBase64());  // Convert ByteArray to Base64 String
        dto.setPublicKey(result.getPublicKeyCose().getBase64());     // Convert ByteArray to Base64 String
        dto.setSignCount(String.valueOf(result.getSignatureCount())); // Convert int to String
        dto.setUserId(String.valueOf(user.getId()));  // Convert Long to String

        return dto;
    }
}
