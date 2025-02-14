package com.webauthn.demo.Controllers;

import com.webauthn.demo.Dto.PublicKeyCredentialCreationOptions;
import com.webauthn.demo.Dto.RegisterRequest;
import com.webauthn.demo.Services.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webAuthn")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register-challenge")
    public ResponseEntity<PublicKeyCredentialCreationOptions> startRegistration(@RequestBody RegisterRequest registerRequest) {
        try {
            return registerService.registerStart(registerRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
