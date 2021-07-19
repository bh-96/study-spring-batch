package com.bh.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
//        SpringApplication.run(SpringBatchApplication.class, args);
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchApplication.class, args)));

        // SpringApplication.exit() and System.exit() : 작업 완료 시 JVM 종료
    }
}
