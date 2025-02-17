package com.webauthn.demo.Controllers;

import com.webauthn.demo.Dto.LoginStartDto;
import com.webauthn.demo.Services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webAuthn")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    @PostMapping("/login-start")
    public ResponseEntity<LoginStartDto> loginStart(@RequestBody  String username) {
        try {
            return loginService.loginStart(username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login-verification")
    public ResponseEntity<Object> loginVerification(@RequestBody String registerRequest) {
        try {
            return loginService.loginVerification(registerRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
