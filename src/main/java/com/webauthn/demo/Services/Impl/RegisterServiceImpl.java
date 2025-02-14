package com.webauthn.demo.Services.Impl;

import com.webauthn.demo.Dto.PublicKeyCredentialCreationOptions;
import com.webauthn.demo.Dto.RegisterRequest;
import com.webauthn.demo.Dto.UserDto;
import com.webauthn.demo.Services.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RegisterServiceImpl implements RegisterService {

    public ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest) {

            PublicKeyCredentialCreationOptions passkeyReq = new PublicKeyCredentialCreationOptions();

            PublicKeyCredentialCreationOptions.Rp rp = passkeyReq.new Rp();
            rp.setName("WebAuthn Demo"); // Set the name of the relying party
            rp.setId("localhost"); // Set the id of the relying party
            passkeyReq.setRp(rp);

            UserDto user = new UserDto();
            user.setUsername("Johne50"); // Set the username
            user.setDisplayName("John Doe"); // Set the display name
            user.setId("U001"); // Set the id
            passkeyReq.setUser(user);

            byte[] challenge = generateChallenge();
            passkeyReq.setChallenge(base64EncodeChallenge(challenge)); // Set the challenge

            PublicKeyCredentialCreationOptions.PublicKeyCredParams publicKeyCredParams = passkeyReq.new PublicKeyCredParams();

            publicKeyCredParams.setType("public-key"); // Set the type
            publicKeyCredParams.setAlg("-7"); // Set the algorithm

            passkeyReq.getPubKeyCredParams().add(publicKeyCredParams);

            passkeyReq.setTimeout(60000L); // Set the timeout
            passkeyReq.setAttestation("none"); // Set the attestation

            PublicKeyCredentialCreationOptions.AuthenticatorSelection authenticatorSelection = passkeyReq.new AuthenticatorSelection();
            authenticatorSelection.setAuthenticatorAttachment("cross-platform"); // Set the authenticator attachment
            authenticatorSelection.setRequireResidentKey(false); // Set the require resident key
            authenticatorSelection.setUserVerification("preferred"); // Set the user verification
            authenticatorSelection.setResidentKey("discouraged"); // Set the resident key
            passkeyReq.setAuthenticatorSelection(authenticatorSelection);

            return ResponseEntity.ok(passkeyReq);
    }

    public byte[] generateChallenge() {
        SecureRandom random = new SecureRandom();
        byte[] challenge = new byte[32];
        random.nextBytes(challenge);
        return challenge;
    }

    public String base64EncodeChallenge(byte[] byteArray) {
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }
}
