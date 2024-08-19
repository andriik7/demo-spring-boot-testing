package com.junit;

import com.junit.models.CollegeStudent;
import com.junit.models.StudentGrades;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ExampleApplicationTest {

    private static int count = 0;

    @Value("${info.app.name}")
    private String appName;

    @Value("${info.app.description}")
    private String appDescription;

    @Value("${info.app.version}")
    private String appVersion;

    @Value("${info.school.name}")
    private String schoolName;

    @Autowired
    private CollegeStudent student;

    @Autowired
    private StudentGrades studentGrades;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    public void beforeEach() {
        count++;
        System.out.println("Testing: " + appName + " which is " + appDescription +
                " Version: " + appVersion + ". Execution of test method " + count);
        student.setFirstname("Andrii");
        student.setLastname("Kuchera");
        student.setEmailAddress("ak47.10.07.06@gmail.com");
        studentGrades.setMathGradeResults(new ArrayList<>(Arrays.asList(97.3, 81.4, 65.0, 100.0, 77.9, 50.0)));
        student.setStudentGrades(studentGrades);
    }

    @Test
    public void addGradeResultsForStudentGrades() {
        assertEquals(471.6, studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults()), "Sum of given grades should be: " + 471.6);
    }

    @ParameterizedTest
    @CsvSource({
            "88.3, 88.2",
            "17.6, 0",
            "100.0, 99.9"
    })
    public void isGradeGreater(double gradeOne, double gradeTwo) {
        String exception = String.format("%f is not greater than %f", gradeOne, gradeTwo);
        assertTrue(student.getStudentGrades().isGradeGreater(gradeOne, gradeTwo), exception);
    }

    @ParameterizedTest
    @CsvSource({
            "88.1, 88.2",
            "17.6, 20.5",
            "10.0, 99.9"
    })
    public void isGradeNotGreater(double gradeOne, double gradeTwo) {
        String exception = String.format("%f is greater than %f", gradeOne, gradeTwo);
        assertFalse(student.getStudentGrades().isGradeGreater(gradeOne, gradeTwo), exception);
    }

    @Test
    public void isNull() {
        assertNull(student.getStudentGrades().checkNull(null));
    }

    @Test
    public void isNotNull() {
        assertNotNull(student.getStudentGrades().checkNull("null"));
    }

    @Test
    public void createStudentWithoutGrade() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        assertNotNull(studentGrades.checkNull(studentTwo));
        studentTwo.setFirstname("John");
        studentTwo.setLastname("Doe");
        studentTwo.setEmailAddress("johndoe@gmail.com");
        assertNotNull(studentTwo.getFirstname());
        assertNotNull(studentTwo.getLastname());
        assertNotNull(studentTwo.getEmailAddress());
        assertNull(studentGrades.checkNull(studentTwo.getStudentGrades()));
    }

    @Test
    public void verifyStudentsArePrototypes() {
        CollegeStudent studentOne = context.getBean("collegeStudent", CollegeStudent.class);
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);

        assertNotSame(studentOne, studentTwo);
    }

    @Test
    public void findAverageGrade() {
        assertAll("Testing all assertEquals"
                ,() -> assertEquals(78.6, studentGrades.findGradePointAverage(student.getStudentGrades().getMathGradeResults()),
                "Average grade must be 78.6"),
                () -> assertEquals(471.6, studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults()),
                        "Sum of given grades should be: " + 471.6)
        );
    }

}
