package com.example.university;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UniversityController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private ProfessorCourseRepository professorCourseRepository;

    /*
    * Register a student
    * Ex -
    * POST /student
    * {
    * "firstName": "Alice",
    * "lastName": "Smith",
    * "contactNumber": 1234,
    * "address": "123, NY"
    * }
    */
    @PostMapping("/student")
    public ResponseEntity<String> createStudent(@RequestBody Student student) {
        if(student.getFirstName() == null) {
            return new ResponseEntity<>("Student's firstName can't be null", HttpStatus.BAD_REQUEST);
        }

        studentRepository.save(student);
        return new ResponseEntity<>("Student record created : "+ student.getFirstName(), HttpStatus.OK);
    }

    /*
     * Register a professor
     * Ex -
     * POST /professor
     * {
     * "firstName": "Bob",
     * "lastName": "Smith",
     * "contactNumber": 1234,
     * "address": "123, NY"
     * }
     */
    @PostMapping("/professor")
    public ResponseEntity<String> createProfessor(@RequestBody Professor professor) {

        if(professor.getFirstName() == null) {
            return new ResponseEntity<>("Professor's firstName can't be null", HttpStatus.BAD_REQUEST);
        }

        professorRepository.save(professor);
        return new ResponseEntity<>("Professor record created : "+ professor.getFirstName(), HttpStatus.OK);
    }

    /*
     * Create a course
     * Ex -
     * POST /course
     * {
     * "courseName": "Physics"
     * }
     */
    @PostMapping("/course")
    public String createCourse(@RequestBody Course course) {
        if(course.getCourseName() != null) {
            courseRepository.save(course);
            return "course created : "+ course.getCourseName();
        } else {
            return "course name cannot be NULL";
        }
    }

    /*
     *  Assign professor to specified course
     *  Ex -
     * POST /assignProfessor
     * {
     * "courseId": 10,
     * "professorId": 2
     * }
     */
    @PostMapping("/assignProfessor")
    public ResponseEntity<String> assignProfessor(@RequestBody ProfessorAssignment professorAssignment) {
        int professorId = professorAssignment.getProfessorId();
        int courseId = professorAssignment.getCourseId();

        if(!professorRepository.existsById(professorId)) {
            return new ResponseEntity<>("Professor with id "+ professorId + " doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        if (!courseRepository.existsById(courseId)) {
            return new ResponseEntity<>("Specified course id " + courseId + " doesn't exist !", HttpStatus.BAD_REQUEST);
        }

        ProfessorAssignment assignedProfessor = professorCourseRepository.findByCourseId(courseId);

        if(assignedProfessor != null) {
            if(assignedProfessor.getProfessorId() == professorId) {
                return new ResponseEntity<>("Professor with id  "+ professorId +" is already assigned to Course id " + courseId, HttpStatus.BAD_REQUEST);
            }

            assignedProfessor.setProfessorId(professorAssignment.getProfessorId());
            professorCourseRepository.save(assignedProfessor);
            return new ResponseEntity<>("Professor with id "+ professorId +" have been assigned to Course id " + courseId, HttpStatus.OK);

        } else {
            professorCourseRepository.save(professorAssignment);
            return new ResponseEntity<>("Professor with id "+ professorId +" have been assigned to Course id " + courseId, HttpStatus.OK);
        }
    }

    /*
     *  Register student to specified course
     *  Ex -
     * POST /registerStudent
     * {
     * "courseId": 1,
     * "studentId": 2
     * }
     */
    @PostMapping("/registerStudent")
    public ResponseEntity<String> registerStudent(@RequestBody StudentCourseRegistration studentCourseRegistration) {
        int courseId = studentCourseRegistration.getCourseId();
        int studentId = studentCourseRegistration.getStudentId();

        if(!studentRepository.existsById(studentId)) {
            return new ResponseEntity<>("Student with "+ studentId + " doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        if(!courseRepository.existsById(courseId)) {
            return new ResponseEntity<>("Course with "+ courseId + " doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        List<StudentCourseRegistration> studentCourseRegistrations = studentCourseRepository.findAllByCourseId(courseId);
        List<Integer> registrations = studentCourseRegistrations.stream().map(registration -> registration.getStudentId()).collect(Collectors.toList());

        if(registrations.contains(studentId)) {
            return new ResponseEntity<>("student: "+ studentId + " is already registered for course: " + courseId, HttpStatus.BAD_REQUEST);
        }

        studentCourseRepository.save(studentCourseRegistration);
        return new ResponseEntity<>("student: "+ studentId + " registered for course: " + courseId, HttpStatus.OK);
    }

    /*
     *  List all the professors
     *  Ex -
     * GET /professors
     */
    @GetMapping("/professors")
    public List<Professor> getProfessors() {
        return professorRepository.findAll();
    }

    /*
     *  List all the students
     *  Ex -
     * GET /students
     */
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    /*
     *  List all the courses
     *  Ex -
     * GET /courses
     */
    @GetMapping("/courses")
    public List<Course> getCourse() {
        return courseRepository.findAll();
    }

    /*
     *  List all courses assigned to a professor
     *  Ex -
     * GET /courses/{professorId}
     */
    @GetMapping("/courses/{professorId}")
    public List<Optional<Course>> getCourseByProfessor(@PathVariable int professorId) {
        List<ProfessorAssignment> professorAssignments = professorCourseRepository.findByProfessorId(professorId);
        return professorAssignments.stream()
                .map(assignedProfessor -> courseRepository.findById(assignedProfessor.getCourseId()))
                .collect(Collectors.toList());
    }

    /*
     *  List all student assigned to a course
     *  Ex -
     * GET /students/{courseId}
     */
    @GetMapping("/students/{courseId}")
    public List<Optional<Student>> getStudentsByCourse(@PathVariable int courseId) {
       List<StudentCourseRegistration> studentCourseRegistrations = studentCourseRepository.findAllByCourseId(courseId);
       return studentCourseRegistrations.stream().map(registration -> studentRepository.findById(registration.getStudentId())).collect(Collectors.toList());
    }
}
