package com.junit;

import com.junit.models.CollegeStudent;
import com.junit.models.StudentGrades;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ReflectionTestUtilsTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades studentGrades;

    @BeforeEach
    public void beforeEach() {
        studentOne.setFirstname("Andrii");
        studentOne.setLastname("Kuchera");
        studentOne.setEmailAddress("ak47.10.07.06@gmail.com");
        studentOne.setStudentGrades(studentGrades);

        ReflectionTestUtils.setField(studentOne, "id", 1);
        ReflectionTestUtils.setField(studentOne, "studentGrades", new StudentGrades(
                new ArrayList<>(Arrays.asList(97.3, 81.4, 65.0, 100.0, 77.9, 50.0))));
    }

    @Test
    public void getDataFromPrivateField() {
        assertEquals(1, ReflectionTestUtils.getField(studentOne, "id"));
    }

    @Test
    public void getDataFromPrivateMethod() {
        assertEquals("Andrii 1",
                ReflectionTestUtils.invokeMethod(studentOne, "getFirstNameAndId"));
    }
}
