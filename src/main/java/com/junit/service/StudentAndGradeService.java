package com.junit.service;

import com.junit.models.*;
import com.junit.repository.HistoryGradeDao;
import com.junit.repository.MathGradeDao;
import com.junit.repository.ScienceGradeDao;
import com.junit.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    StudentDao studentDao;

    @Autowired
    MathGradeDao mathGradeDao;

    @Autowired
    HistoryGradeDao historyGradeDao;

    @Autowired
    ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("mathGrades")
    MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    HistoryGrade historyGrade;

    private static int studentCreationId = 1;

    private static int mathGradeCreationId = 1;

    private static int scienceGradeCreationId = 1;

    private static int historyGradeCreationId = 1;

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(studentCreationId++);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {

        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isEmpty();
    }

    public void deleteStudent(int id) {

        if (!checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
            mathGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String subject) {

        if (checkIfStudentIsNull(studentId))
            return false;
        if (grade > 0 && grade <= 100) {
            switch (subject) {
                case "math" -> {
                    mathGrade.setId(mathGradeCreationId++);
                    mathGrade.setGrade(grade);
                    mathGrade.setStudentId(studentId);
                    mathGradeDao.save(mathGrade);
                }
                case "history" -> {
                    historyGrade.setId(historyGradeCreationId++);
                    historyGrade.setGrade(grade);
                    historyGrade.setStudentId(studentId);
                    historyGradeDao.save(historyGrade);
                }
                case "science" -> {
                    scienceGrade.setId(scienceGradeCreationId++);
                    scienceGrade.setGrade(grade);
                    scienceGrade.setStudentId(studentId);
                    scienceGradeDao.save(scienceGrade);
                }
                default -> {
                    return false;
                }
            }
        } else return false;
        return true;
    }

    public int deleteGrade(int id, String subject) {
        int studentId = -1;
        switch (subject) {
            case "math" -> {
                Optional<MathGrade> grade = mathGradeDao.findById(id);
                if (grade.isEmpty()) return studentId;
                mathGradeDao.deleteById(id);
                studentId = grade.get().getStudentId();
            }
            case "history" -> {
                Optional<HistoryGrade> grade = historyGradeDao.findById(id);
                if (grade.isEmpty()) return studentId;
                historyGradeDao.deleteById(id);
                studentId = grade.get().getStudentId();
            }
            case "science" -> {
                Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
                if (grade.isEmpty()) return studentId;
                scienceGradeDao.deleteById(id);
                studentId = grade.get().getStudentId();
            }
            default -> {
                return studentId;
            }
        }
        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int id) throws IllegalArgumentException {

        Optional<CollegeStudent> optionalStudent = studentDao.findById(id);
        if (optionalStudent.isEmpty()) return null;
        CollegeStudent student = optionalStudent.get();

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(id);

        List<Grade> genericMathGrades = new ArrayList<>();
        mathGrades.forEach(genericMathGrades::add);
        List<Grade> genericHistoryGrades = new ArrayList<>();
        historyGrades.forEach(genericHistoryGrades::add);
        List<Grade> genericScienceGrades = new ArrayList<>();
        scienceGrades.forEach(genericScienceGrades::add);

        StudentGrades studentGrades = new StudentGrades();
        studentGrades.setMathGradeResults(genericMathGrades);
        studentGrades.setHistoryGradeResults(genericHistoryGrades);
        studentGrades.setScienceGradeResults(genericScienceGrades);

        return new GradebookCollegeStudent(student.getId(), student.getFirstname(), student.getLastname(), student.getEmailAddress(), studentGrades);
    }
}
