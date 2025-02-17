package com.webauthn.demo.Services.Impl;

import com.webauthn.demo.Dto.LoginStartDto;
import com.webauthn.demo.Services.LoginService;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.verifier.exception.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

    public ResponseEntity<LoginStartDto> loginStart(String username) {

        LoginStartDto loginStartDto = new LoginStartDto();
        loginStartDto.setChallenge(new DefaultChallenge()); // save this new challenge to verify later
        loginStartDto.setTimeout(60000L);
        loginStartDto.setRpId("localhost");
        loginStartDto.setAllowCredentials(List.of());
        loginStartDto.setUserVerification("preferred");

        return ResponseEntity.ok(loginStartDto);
    }

    public ResponseEntity<Object> loginVerification(String loginRequest) {
        AuthenticationData authenticationData;

        try {
            authenticationData = webAuthnManager.parseAuthenticationResponseJSON(loginRequest);

        }
        catch (DataConversionException e) {
            log.error("Invalid request - {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid request - " + e.getMessage());
        }

        Origin origin = new Origin("http://localhost:3000"); // define frontend origin
        String rpId = "localhost";
        Challenge challenge = new DefaultChallenge();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        List<byte[]> allowedCredentials = null;
        boolean userVerificationRequired = true;
        boolean userPresenceRequired = true;

        CredentialRecord credentialRecord = load(authenticationData.getCredentialId()); // Load the credential record from the database against the credential id
        AuthenticationParameters authenticationParameters = new AuthenticationParameters(
                serverProperty,
                credentialRecord,
                allowedCredentials,
                userVerificationRequired,
                userPresenceRequired
        );

        try {
            webAuthnManager.verify(authenticationData, authenticationParameters);
        }
        catch (VerificationException e) {
            log.error("Verification failed - {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid request - " + e.getMessage());
        }
        return ResponseEntity.ok("login successfully"); // Return user details for valid login
    }
}
