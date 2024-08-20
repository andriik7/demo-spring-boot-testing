package com.junit.repository;

import com.junit.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao extends CrudRepository<CollegeStudent, Integer> {

    CollegeStudent findByEmailAddress(String emailAddress);
}
