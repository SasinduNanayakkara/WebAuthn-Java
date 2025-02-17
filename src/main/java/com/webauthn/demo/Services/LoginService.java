package com.webauthn.demo.Services;

import com.webauthn.demo.Dto.LoginStartDto;
import com.webauthn.demo.Dto.RegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity<LoginStartDto> loginStart(String username);

    ResponseEntity<Object> loginVerification(String loginRequest);
}
