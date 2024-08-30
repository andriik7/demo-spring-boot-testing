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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource("/application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class GradebookControllerTest {

    private static MockHttpServletRequest mockHttpRequest;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDao studentDao;

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

    @BeforeAll
    public static void beforeAll() {
        mockHttpRequest = new MockHttpServletRequest();
        mockHttpRequest.setParameter("firstname", "Chad");
        mockHttpRequest.setParameter("lastname", "Darby");
        mockHttpRequest.setParameter("emailAddress", "chad.darby@gmail.com");
    }

    @BeforeEach
    public void BeforeEach() {
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
    public void getStudentsWithHttpRequestTest() throws Exception {

        CollegeStudent studentOne = new GradebookCollegeStudent(
                "Eric", "Roby", "eric.roby@gmail.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent(
                "Chad", "Darby", "chad.darby@gmail.com");

        List<CollegeStudent> collegeStudents = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudents);

        assertIterableEquals(collegeStudents, studentCreateServiceMock.getGradebook(), "Service returns wrong collection of students");

        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentWithHttpRequestTest() throws Exception {

        CollegeStudent studentOne = new CollegeStudent(
                "Eric", "Roby", "eric.roby@gmail.com");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(students);

        assertIterableEquals(students, studentCreateServiceMock.getGradebook());

        MvcResult result = mockMvc.perform(post("/")
                        .param("firstname", mockHttpRequest.getParameterValues("firstname"))
                        .param("lastname", mockHttpRequest.getParameterValues("lastname"))
                        .param("emailAddress", mockHttpRequest.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao
                .findByEmailAddress("chad.darby@gmail.com");

        assertNotNull(verifyStudent, "Student must not be null");
    }

    @Test
    public void deleteStudentWithHttpRequestTest() throws Exception {
        int id = 10;

        assertTrue(studentDao.findById(id).isPresent());

        MvcResult result = mockMvc.perform(get("/delete/student/{id}", id))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        assertFalse(studentDao.findById(id).isPresent());
    }

    @Test
    public void deleteStudentWithHttpRequestForErrorPageTest() throws Exception {
        int id = 0;

        MvcResult result = mockMvc.perform(get("/delete/student/{id}", id))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void getStudentInfoWithHttpRequestTest() throws Exception {

        int id = 10;

        assertTrue(studentDao.findById(id).isPresent());

        MvcResult result = mockMvc.perform(get("/studentInformation/{id}", id))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    public void getStudentInfoWithHttpRequestAndInvalidIdTest() throws Exception {

        int id = 0;

        assertFalse(studentDao.findById(id).isPresent());

        MvcResult result = mockMvc.perform(get("/studentInformation/{id}", id))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @ParameterizedTest
    @CsvSource(
            {"math, 96.30", "history, 66.20", "science, 74.90"}
    )
    public void createGradeWithHttpRequestTest(String subject, String grade) throws Exception {
        int id = 10;

        assertTrue(studentDao.findById(id).isPresent());

        GradebookCollegeStudent student = studentService.studentInformation(id);

        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, student.getStudentGrades().getHistoryGradeResults().size());
        assertEquals(1, student.getStudentGrades().getScienceGradeResults().size());

        MvcResult result = mockMvc.perform(post("/grades")
                        .param("grade", grade)
                        .param("studentId", String.valueOf(id))
                        .param("gradeType", subject))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        student = studentService.studentInformation(id);

        switch (subject) {
            case "math" -> {
                assertEquals(2, student.getStudentGrades().getMathGradeResults().size());
            }
            case "history" -> {
                assertEquals(2, student.getStudentGrades().getHistoryGradeResults().size());
            }
            case "science" -> {
                assertEquals(2, student.getStudentGrades().getScienceGradeResults().size());
            }
        }
    }

    @ParameterizedTest
    @CsvSource(
            {"math, 96.30", "history, 66.20", "science, 74.90"}
    )
    public void createGradeWithHttpRequestAndInvalidStudentIdTest(String subject, String grade) throws Exception {

        MvcResult result = mockMvc.perform(post("/grades")
                        .param("grade", grade)
                        .param("studentId", "0")
                        .param("gradeType", subject))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @ParameterizedTest
    @CsvSource(
            {"english, 96.30", "historyy, 66.20", "literature, 74.90"}
    )
    public void createGradeWithHttpRequestAndInvalidSubjectTest(String subject, String grade) throws Exception {

        MvcResult result = mockMvc.perform(post("/grades")
                        .param("grade", grade)
                        .param("studentId", "10")
                        .param("gradeType", subject))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @ParameterizedTest
    @CsvSource(
            {"math", "history", "science"}
    )
    public void deleteGradeWithHttpRequestTest(String gradeType) throws Exception {
        int gradeId = 10;

        MathGrade mathGrade = null;

        HistoryGrade historyGrade = null;

        ScienceGrade scienceGrade = null;

        switch (gradeType) {
            case "math" -> {
                Optional<MathGrade> mathGradeOptional = mathGradeDao.findById(gradeId);
                assertTrue(mathGradeOptional.isPresent());
                mathGrade = mathGradeOptional.get();
            }
            case "history" -> {
                Optional<HistoryGrade> historyGradeOptional = historyGradeDao.findById(gradeId);
                assertTrue(historyGradeOptional.isPresent());
                historyGrade = historyGradeOptional.get();
            }
            case "science" -> {
                Optional<ScienceGrade> scienceGradeOptional = scienceGradeDao.findById(gradeId);
                assertTrue(scienceGradeOptional.isPresent());
                scienceGrade = scienceGradeOptional.get();
            }
            default -> fail();
        }

        MvcResult result = mockMvc.perform(get("/grades/{gradeType}/{gradeId}", gradeType, gradeId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        if (mathGrade != null) {
            assertTrue(mathGradeDao.findById(gradeId).isEmpty());
        } else if (historyGrade != null) {
            assertTrue(historyGradeDao.findById(gradeId).isEmpty());
        } else if (scienceGrade != null) {
            assertTrue(scienceGradeDao.findById(gradeId).isEmpty());
        }
    }

    @ParameterizedTest
    @CsvSource(
            {"math, 0", "history, 300", "science, -1"}
    )
    public void deleteGradeWithHttpRequestAndInvalidId(String gradeType, int gradeId) throws Exception {

        switch (gradeType) {
            case "math" -> {
                Optional<MathGrade> mathGradeOptional = mathGradeDao.findById(gradeId);
                assertTrue(mathGradeOptional.isEmpty());
            }
            case "history" -> {
                Optional<HistoryGrade> historyGradeOptional = historyGradeDao.findById(gradeId);
                assertTrue(historyGradeOptional.isEmpty());
            }
            case "science" -> {
                Optional<ScienceGrade> scienceGradeOptional = scienceGradeDao.findById(gradeId);
                assertTrue(scienceGradeOptional.isEmpty());
            }
            default -> fail();
        }

        MvcResult result = mockMvc.perform(get("/grades/{gradeType}/{gradeId}", gradeType, gradeId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");

    }

    @ParameterizedTest
    @CsvSource(
            {"english", "historyy", "literature"}
    )
    public void deleteGradeWithHttpRequestAndInvalidGradeType(String gradeType) throws Exception {

        int gradeId = 10;

        MvcResult result = mockMvc.perform(get("/grades/{gradeType}/{gradeId}", gradeType, gradeId))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }
}
