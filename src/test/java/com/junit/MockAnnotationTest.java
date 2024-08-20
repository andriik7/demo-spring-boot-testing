package com.junit;

import com.junit.dao.ApplicationDao;
import com.junit.models.CollegeStudent;
import com.junit.models.StudentGrades;
import com.junit.service.ApplicationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayNameGeneration(CamelCaseDisplay.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MockAnnotationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CollegeStudent studentOne;

    @Autowired
    private StudentGrades studentGrades;

    @MockBean
    private ApplicationDao dao;

    @Autowired
    private ApplicationService service;

    @BeforeEach
    public void beforeEach() {
        studentOne.setFirstname("Andrii");
        studentOne.setLastname("Kuchera");
        studentOne.setEmailAddress("ak47.10.07.06@gmail.com");
        //studentGrades.setMathGradeResults(new ArrayList<>(Arrays.asList(97.3, 81.4, 65.0, 100.0, 77.9, 50.0)));
        studentOne.setStudentGrades(studentGrades);
    }

    @Test
    public void whenAndVerify() {
         when(dao.addGradeResultsForSingleClass(
                 studentGrades.getMathGradeResults())).thenReturn(100.0);

         assertEquals(100.0, service.addGradeResultsForSingleClass(
                 studentGrades.getMathGradeResults()));

         verify(dao, times(1  )).addGradeResultsForSingleClass(studentGrades.getMathGradeResults());
    }

    @Test
    public void findAverageGradePoint() {
        when(dao.findGradePointAverage(
                studentGrades.getMathGradeResults())).thenReturn(55.4);

        assertEquals(55.4, service.findGradePointAverage(studentGrades.getMathGradeResults()));

        verify(dao, times(1)).findGradePointAverage(studentGrades.getMathGradeResults());
    }

    @Test
    public void nullableCheck() {
        when(dao.checkNull(studentGrades.getMathGradeResults())).thenReturn(true);

        assertNotNull(service.checkNull(studentOne.getStudentGrades().getMathGradeResults()));
    }

    @Test
    public void checkExceptionThrow() {
        CollegeStudent studentNull = context.getBean("collegeStudent", CollegeStudent.class);

        when(dao.checkNull(studentNull)).thenThrow(new RuntimeException()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.checkNull(studentNull));

        assertNull(service.checkNull(studentNull));

        verify(dao, times(2)).checkNull(studentNull);
    }
}
