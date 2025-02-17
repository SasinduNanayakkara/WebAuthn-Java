package com.webauthn.demo.Services.Impl;

import com.webauthn.demo.Dto.RegisterRequest;
import com.webauthn.demo.Services.RegisterService;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
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
public class RegisterServiceImpl implements RegisterService {
    private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

    public ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest) {

        PublicKeyCredentialCreationOptions passkeyReq = new PublicKeyCredentialCreationOptions(
                new PublicKeyCredentialRpEntity("localhost", "WebAuthn Demo"), //  provide your domain name and display name
                new PublicKeyCredentialUserEntity(registerRequest.getUsername().getBytes(), registerRequest.getUsername(), "John Doe"), // Provide user id, name and display name
                new DefaultChallenge(), // generate new challenge and save it for verification
                List.of(
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
                ),
                60000L, // Timeout
                List.of(), // Allowed credentials should be explicitly typed if needed
                new AuthenticatorSelectionCriteria(AuthenticatorAttachment.PLATFORM, false, UserVerificationRequirement.PREFERRED),
                List.of(), // Hints
                AttestationConveyancePreference.NONE,
                null // Extensions (pass null if unused)
        );

        return ResponseEntity.ok(passkeyReq);
    }

    public ResponseEntity<?> registerValidate(String registerRequest) {

        RegistrationData registrationData;

        try {
            registrationData = webAuthnManager.parseRegistrationResponseJSON(registerRequest);
        } catch (DataConversionException e) {
            log.error("Invalid request - {}", e.getMessage());
            return ResponseEntity.status(400).body("Invalid request - " + e.getMessage());
        }

        Origin origin = new Origin("http://localhost:3000"); //provide your domain name
        String rpId = "localhost"; //provide your domain name
        Challenge challenge = new DefaultChallenge(); //get the challenge from the database against the user
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
        ); //provide the allowed algorithms for the generated public key
        boolean userVerificationRequired = false;
        boolean userPresenceRequired = true;

        RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, pubKeyCredParams, userVerificationRequired, userPresenceRequired);

        try {
            webAuthnManager.verify(registrationData, registrationParameters);
        } catch (VerificationException e) {
            log.error("Credentials verify failed - {}", e.getMessage());
            return ResponseEntity.status(400).body("Credentials verify failed - " + e.getMessage());
        }

        CredentialRecord credentialRecord = new CredentialRecordImpl(
                registrationData.getAttestationObject(),
                registrationData.getCollectedClientData(),
                registrationData.getClientExtensions(),
                registrationData.getTransports()
        );

        save(credentialRecord); // Save the credential record to the database against the credential id
        return ResponseEntity.ok("verified: true");
    }



}
