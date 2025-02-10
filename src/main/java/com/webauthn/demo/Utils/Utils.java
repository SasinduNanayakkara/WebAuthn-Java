package com.webauthn.demo.Utils;

import com.yubico.webauthn.data.ByteArray;

public class Utils {

    public byte[] convertToDatabaseColumn(ByteArray attribute) {
        return attribute.getBytes();
    }

    public ByteArray convertToEntityAttribute(byte[] dbData) {
        return new ByteArray(dbData);
    }
}
