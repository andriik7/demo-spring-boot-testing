package com.junit;

import com.junit.display.CamelCaseDisplay;
import com.junit.models.CollegeStudent;
import com.junit.repository.StudentDao;
import com.junit.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    public void beforeEach() {
        jdbc.execute("insert into student(id, firstname, lastname, email_address) " +
                "values(1, 'Andrii', 'Kuchera', 'ak47.10.07.06@gmail.com')");
    }

    @AfterEach
    public void afterEach() {
        jdbc.execute("DELETE FROM student");
    }

    @Test
    public void createStudentTest() {

        CollegeStudent student = studentDao.findByEmailAddress("ak47.10.07.06@gmail.com");

        assertEquals("ak47.10.07.06@gmail.com", student.getEmailAddress(), "Student email must be \'ak47.10.07.06@gmail.com\'");
    }

    @Test
    public void isStudentNullCheck() {

        assertFalse(studentService.checkIfStudentIsNull(1), "Student with id 1 presents");

        assertTrue(studentService.checkIfStudentIsNull(0), "Student with id 0 doesn't present");
    }

    @Test
    public void deleteStudentTest() {

        Optional<CollegeStudent> deletedStudent = studentDao.findById(1);

        assertTrue(deletedStudent.isPresent());

        studentService.deleteStudent(1);

        deletedStudent = studentDao.findById(1);

        assertFalse(deletedStudent.isPresent());
    }
    @Sql("/insertData.sql")
    @Test
    public void getGradebookTest() {
        Iterable<CollegeStudent> iterableCollegeStudent = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent student : iterableCollegeStudent) {
            collegeStudents.add(student);
        }

        assertEquals(5, collegeStudents.size());
    }
}
