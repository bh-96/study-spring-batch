package com.bh.study.domain;

import lombok.Data;

import javax.persistence.Id;

@Data
public class Person {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private int age;

    private String recvURL;

    private String sendYn;
}
