package com.webauthn.demo.Services;

import com.webauthn.demo.Dto.RegisterRequest;
import com.webauthn.demo.Dto.RegistrationRequest;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import org.springframework.http.ResponseEntity;

public interface RegisterService {

    ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest);
    ResponseEntity<?> registerValidate(String registerRequest);
    ResponseEntity<?> loginVerification(RegistrationRequest registerRequest);
}
