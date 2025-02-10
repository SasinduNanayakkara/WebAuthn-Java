package com.webauthn.demo.Dto;

import com.yubico.webauthn.data.ByteArray;
import lombok.Data;

@Data
public class UserDto {
     private Long id;
     private String username;
     private String displayName;
     private ByteArray handle;
}
