package com.junit.controller;

import com.junit.models.CollegeStudent;
import com.junit.models.Gradebook;
import com.junit.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {

        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students", collegeStudents);

        return "index";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {

        studentService.createStudent(
                student.getFirstname(), student.getLastname(), student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students", collegeStudents);

        return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {

        if (studentService.checkIfStudentIsNull(id)) return "error";

        studentService.updateStudentInfoInModel(id, m);

        return "studentInformation";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {

        if (studentService.checkIfStudentIsNull(id))
            return "error";

        studentService.deleteStudent(id);
        m.addAttribute("students", studentService.getGradebook());

        return "index";
    }

    @PostMapping("/grades")
    public String addGrade(@RequestParam("grade") double grade,
                           @RequestParam("studentId") int studentId,
                           @RequestParam("gradeType") String subject,
                           Model m) {

        boolean successfulAddition = studentService.createGrade(grade, studentId, subject);

        if (!successfulAddition) return "error";

        studentService.updateStudentInfoInModel(studentId, m);

        return "studentInformation";
    }

    @GetMapping("/grades/{gradeType}/{id}")
    public String deleteGrade(@PathVariable String gradeType, @PathVariable int id, Model m) {

        int studentId = studentService.deleteGrade(id, gradeType);

        if (studentId == -1) return "error";

        studentService.updateStudentInfoInModel(studentId, m);

        return "studentInformation";
    }

}