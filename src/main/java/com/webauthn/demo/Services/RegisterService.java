package com.webauthn.demo.Services;

import com.webauthn.demo.Dto.PublicKeyCredentialCreationOptions;
import com.webauthn.demo.Dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface RegisterService {

    ResponseEntity<PublicKeyCredentialCreationOptions> registerStart(RegisterRequest registerRequest);
}
