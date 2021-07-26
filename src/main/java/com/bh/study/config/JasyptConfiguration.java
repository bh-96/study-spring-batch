package com.bh.study.config;

import com.bh.study.common.Const;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * properties 암호화
 */
@Configuration
public class JasyptConfiguration {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(Const.ENCRYPT_KEY);          // 암호화에 사용할 키
        config.setAlgorithm(Const.ENCRYPT_ALGORITHM);   // 사용할 알고리즘
        config.setPoolSize("1");
        encryptor.setConfig(config);
        return encryptor;
    }
}
