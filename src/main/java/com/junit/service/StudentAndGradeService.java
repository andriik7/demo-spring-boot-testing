package com.junit.service;

import com.junit.models.CollegeStudent;
import com.junit.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    StudentDao studentDao;

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(1);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {

        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isEmpty();
    }

    public void deleteStudent(int id) {

        if (!checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }
}
