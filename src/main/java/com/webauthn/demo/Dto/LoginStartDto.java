package com.webauthn.demo.Dto;

import com.webauthn4j.data.client.challenge.Challenge;
import lombok.Data;

import java.util.List;

@Data
public class LoginStartDto {

    private Challenge challenge;
    private Long timeout;
    private String rpId;
    private List<String> allowCredentials;
    private String userVerification;
}
