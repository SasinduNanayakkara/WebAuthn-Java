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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.webauthn.demo.Dto.RegistrationRequest;

import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

//    public ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest) {
//
//            PublicKeyCredentialCreationOptions passkeyReq = new PublicKeyCredentialCreationOptions();
//
//            PublicKeyCredentialCreationOptions.Rp rp = passkeyReq.new Rp();
//            rp.setName("WebAuthn Demo"); // Set the name of the relying party
//            rp.setId("localhost"); // Set the id of the relying party
//            passkeyReq.setRp(rp);
//
//            UserDto user = new UserDto();
//            user.setUsername("Johne50"); // Set the username
//            user.setDisplayName("John Doe"); // Set the display name
//            user.setId("U001"); // Set the id
//            passkeyReq.setUser(user);
//
//            byte[] challenge = generateChallenge();
//            passkeyReq.setChallenge(base64EncodeChallenge(challenge)); // Set the challenge
//
//            PublicKeyCredentialCreationOptions.PublicKeyCredParams publicKeyCredParams = passkeyReq.new PublicKeyCredParams();
//
//            publicKeyCredParams.setType("public-key"); // Set the type
//            publicKeyCredParams.setAlg("-7"); // Set the algorithm
//
//            passkeyReq.getPubKeyCredParams().add(publicKeyCredParams);
//
//            passkeyReq.setTimeout(60000L); // Set the timeout
//            passkeyReq.setAttestation("none"); // Set the attestation
//
//            PublicKeyCredentialCreationOptions.AuthenticatorSelection authenticatorSelection = passkeyReq.new AuthenticatorSelection();
//            authenticatorSelection.setAuthenticatorAttachment("cross-platform"); // Set the authenticator attachment
//            authenticatorSelection.setRequireResidentKey(false); // Set the require resident key
//            authenticatorSelection.setUserVerification("preferred"); // Set the user verification
//            authenticatorSelection.setResidentKey("discouraged"); // Set the resident key
//            passkeyReq.setAuthenticatorSelection(authenticatorSelection);
//
//            return ResponseEntity.ok(passkeyReq);
//    }

    public ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest) {

        PublicKeyCredentialCreationOptions passkeyReq = new PublicKeyCredentialCreationOptions(
                new PublicKeyCredentialRpEntity("localhost", "WebAuthn Demo"), // Correct order
                new PublicKeyCredentialUserEntity("U001".getBytes(), "U001", "John Doe"), // ID, Name, Display Name
                new DefaultChallenge(), // Ensure this returns a byte[]
                List.of(
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                        new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
                ),
                60000L,
                List.of(), // Allowed credentials should be explicitly typed if needed
                new AuthenticatorSelectionCriteria(AuthenticatorAttachment.PLATFORM, false, UserVerificationRequirement.PREFERRED),
                AttestationConveyancePreference.NONE,
                null // Extensions (pass null if unused)
        );

        return ResponseEntity.ok(passkeyReq);
    }

    public ResponseEntity<?> registerValidate(RegistrationRequest registerRequest) {

        RegistrationData registrationData;

        try {
            registrationData = webAuthnManager.parseRegistrationResponseJSON(registerRequest.toString()); //TODO: get the request as JSON string
        } catch (DataConversionException e) {
            return ResponseEntity.badRequest().build();
        }

        Origin origin = new Origin("http://localhost:8080");
        String rpId = "localhost";
        Challenge challenge = new DefaultChallenge();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256),
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256)
        );
        boolean userVerificationRequired = false;
        boolean userPresenceRequired = true;

        RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, pubKeyCredParams, userVerificationRequired, userPresenceRequired);

        try {
            webAuthnManager.verify(registrationData, registrationParameters);
        } catch (VerificationException e) {
            return ResponseEntity.badRequest().build();
        }

        CredentialRecord credentialRecord = new CredentialRecordImpl(
                registrationData.getAttestationObject(),
                registrationData.getCollectedClientData(),
                registrationData.getClientExtensions(),
                registrationData.getTransports()
        );

        save(); // Save the credential record //TODO: Save the response to the database

        return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> loginVerification(RegistrationRequest registerRequest) {
        AuthenticationData authenticationData;

        try {
            authenticationData = webAuthnManager.parseAuthenticationResponseJSON(registerRequest.toString()); //TODO: get the request as JSON string

        }
        catch (DataConversionException e) {
            return ResponseEntity.badRequest().build();
        }

        Origin origin = new Origin("http://localhost:8080");
        String rpId = "localhost";
        Challenge challenge = new DefaultChallenge();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

        List<byte[]> allowedCredentials = null; //TODO: Load the allowed credentials
        boolean userVerificationRequired = true;
        boolean userPresenceRequired = true;

        CredentialRecord credentialRecord = load(authenticationData.getCredentialId()); //TODO: Load the credential record

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
            return ResponseEntity.badRequest().build();
        }

        updateCounter(authenticationData.getCredentialId(),
                authenticationData.getAuthenticatorData().getSignCount()); //TODO: Update the counter

        return ResponseEntity.ok().build(); //TODO: Return user details for valid login
    }
}
