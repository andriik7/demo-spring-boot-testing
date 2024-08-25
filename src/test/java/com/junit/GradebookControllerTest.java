package com.junit;

import com.junit.display.CamelCaseDisplay;
import com.junit.models.CollegeStudent;
import com.junit.models.GradebookCollegeStudent;
import com.junit.repository.StudentDao;
import com.junit.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource("/application.properties")
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

    @BeforeAll
    public static void beforeAll() {
        mockHttpRequest = new MockHttpServletRequest();
        mockHttpRequest.setParameter("firstname", "Chad");
        mockHttpRequest.setParameter("lastname", "Darby");
        mockHttpRequest.setParameter("emailAddress", "chad.darby@gmail.com");
    }

    @BeforeEach
    public void BeforeEach() {
        jdbc.execute("insert into student(id, firstname, lastname, email_address) " +
                "values(1, 'Andrii', 'Kuchera', 'ak47.10.07.06@gmail.com')");
    }

    @AfterEach
    public void afterEach() {
        jdbc.execute("DELETE FROM student");
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
        int id = 1;

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
}
