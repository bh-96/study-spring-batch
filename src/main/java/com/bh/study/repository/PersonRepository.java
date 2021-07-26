package com.bh.study.repository;

import com.bh.study.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, String> {

    List<Person> findAllByAgeAfter(int age);
}
