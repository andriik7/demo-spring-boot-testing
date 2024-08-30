package com.junit;

import com.junit.display.CamelCaseDisplay;
import com.junit.models.*;
import com.junit.repository.HistoryGradeDao;
import com.junit.repository.MathGradeDao;
import com.junit.repository.ScienceGradeDao;
import com.junit.repository.StudentDao;
import com.junit.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Value("${sql.script.create.student}")
    private String createStudentScript;

    @Value("${sql.script.delete.student}")
    private String deleteStudentScript;

    @Value("${sql.script.create.math.grade}")
    private String createMathGradeScript;

    @Value("${sql.script.create.history.grade}")
    private String createHistoryGradeScript;

    @Value("${sql.script.create.science.grade}")
    private String createScienceGradeScript;

    @Value("${sql.script.delete.math.grade}")
    private String deleteMathGradeScript;

    @Value("${sql.script.delete.history.grade}")
    private String deleteHistoryGradeScript;

    @Value("${sql.script.delete.science.grade}")
    private String deleteScienceGradeScript;


    @BeforeEach
    public void beforeEach() {
        jdbc.execute(createStudentScript);
        jdbc.execute(createMathGradeScript);
        jdbc.execute(createHistoryGradeScript);
        jdbc.execute(createScienceGradeScript);
    }

    @AfterEach
    public void afterEach() {
        jdbc.execute(deleteStudentScript);
        jdbc.execute(deleteMathGradeScript);
        jdbc.execute(deleteHistoryGradeScript);
        jdbc.execute(deleteScienceGradeScript);
    }

    @Test
    public void createStudentTest() {

        CollegeStudent student = studentDao.findByEmailAddress("ak47.10.07.06@gmail.com");

        assertEquals("ak47.10.07.06@gmail.com", student.getEmailAddress(), "Student email must be \'ak47.10.07.06@gmail.com\'");
    }

    @Test
    public void isStudentNullCheck() {

        assertFalse(studentService.checkIfStudentIsNull(10), "Student with id 1 presents");

        assertTrue(studentService.checkIfStudentIsNull(0), "Student with id 0 doesn't present");
    }

    @Test
    public void deleteStudentTest() {

        Optional<CollegeStudent> deletedStudent = studentDao.findById(10);

        assertTrue(deletedStudent.isPresent());

        List<MathGrade> mathGrades = (List<MathGrade>) mathGradeDao.findGradeByStudentId(10);
        List<HistoryGrade> historyGrades = (List<HistoryGrade>) historyGradeDao.findGradeByStudentId(10);
        List<ScienceGrade> scienceGrades = (List<ScienceGrade>) scienceGradeDao.findGradeByStudentId(10);

        assertFalse(mathGrades.isEmpty());
        assertFalse(historyGrades.isEmpty());
        assertFalse(scienceGrades.isEmpty());
        int mathGradeId = mathGrades.getFirst().getId();
        int historyGradeId = historyGrades.getFirst().getId();
        int scienceGradeId = scienceGrades.getFirst().getId();

        studentService.deleteStudent(10);

        deletedStudent = studentDao.findById(10);

        assertFalse(deletedStudent.isPresent());
        assertFalse(mathGradeDao.findById(mathGradeId).isPresent());
        assertFalse(historyGradeDao.findById(historyGradeId).isPresent());
        assertFalse(scienceGradeDao.findById(scienceGradeId).isPresent());
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

    @ParameterizedTest
    @CsvSource({"math", "history", "science"})
    public void createGradeTest(String subject) {
        assertTrue(studentService.createGrade(80.5, 10, subject));

        switch (subject) {
            case "math" -> {
                Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(10);

                assertEquals(2, ((Collection<MathGrade>) mathGrades).size(), "Student actually has math grades");
            }
            case "history" -> {
                Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(10);

                assertEquals(2, ((Collection<HistoryGrade>) historyGrades).size(), "Student actually has history grades");
            }
            case "science" -> {
                Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(10);

                assertEquals(2, ((Collection<ScienceGrade>) scienceGrades).size(), "Student actually has science grades");
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
            {"105, 0, PE", "0, 2, literature"}
    )
    public void createGradeReturnFalseTest(double wrongGrade, int wrongStudentId, String wrongSubject) {
        assertFalse(studentService.createGrade(wrongGrade, 10, "math"));
        assertFalse(studentService.createGrade(88, wrongStudentId, "math"));
        assertFalse(studentService.createGrade(88, 10, wrongSubject));
    }

    @ParameterizedTest
    @CsvSource(
            {"math", "science", "history"}
    )
    public void deleteGradeTest(String subject) {
        assertEquals(10, studentService.deleteGrade(10, subject));
        switch (subject) {
            case "math" -> {
                assertFalse(mathGradeDao.findById(10).isPresent());
            }
            case "history" -> {
                assertFalse(historyGradeDao.findById(10).isPresent());
            }
            case "science" -> {
                assertFalse(scienceGradeDao.findById(10).isPresent());
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
            {"math", "science", "history"}
    )
    public void deleteGradeWithInvalidIdOrSubjectTest(String subject) {
        assertEquals(-1, studentService.deleteGrade(0, subject), "No such grade found");
        assertEquals(-1, studentService.deleteGrade(0, "english"), "No such grade found");
    }

    @Test
    public void retrieveStudentInfoTest() {

        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(10);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(10, gradebookCollegeStudent.getId());
        assertEquals("Andrii", gradebookCollegeStudent.getFirstname());
        assertEquals("Kuchera", gradebookCollegeStudent.getLastname());
        assertEquals("ak47.10.07.06@gmail.com", gradebookCollegeStudent.getEmailAddress());
        assertEquals(1 ,gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1 ,gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
        assertEquals(1 ,gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
    }

    @ParameterizedTest
    @CsvSource(
            {"0", "-1", "300"}
    )
    public void retrieveStudentInfoWithInvalidIdTest(int invalidId) {

        assertNull(studentService.studentInformation(invalidId));
    }
}
