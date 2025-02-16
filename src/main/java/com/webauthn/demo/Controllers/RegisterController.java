package com.webauthn.demo.Controllers;

import com.webauthn.demo.Dto.RegisterRequest;
import com.webauthn.demo.Dto.RegistrationRequest;
import com.webauthn.demo.Services.RegisterService;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
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

    @PostMapping("/register-validate")
    public ResponseEntity<?> registerValidate(@RequestBody String registerRequest) {
        try {
            return registerService.registerValidate(registerRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
