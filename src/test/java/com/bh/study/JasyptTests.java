package com.bh.study;

import com.bh.study.common.Const;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class JasyptTests {

    @Test
    public void encryptTest() {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm(Const.ENCRYPT_ALGORITHM);
        pbeEnc.setPassword(Const.ENCRYPT_KEY);

        String encData = pbeEnc.encrypt("test");
        String decData = pbeEnc.decrypt(encData);

        System.out.println("Encrypt \'" + decData + "\' = ENC(" + encData + ")");
    }
}