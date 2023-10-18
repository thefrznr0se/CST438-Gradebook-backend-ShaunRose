package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${registration.url}") 
    String registration_url;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    public RegistrationServiceREST() {
        System.out.println("REST registration service");
    }

    @Override
    public void sendFinalGrades(int course_id, FinalGradeDTO[] grades) {
        // TODO: Use restTemplate to send final grades to the registration service
        System.out.println("Sending Final Grades: ");
        // You need to implement the logic for sending final grades.
        // Example: restTemplate.postForObject(registration_url + "/finalGrades/" + course_id, grades, Object.class);
    }

    @PostMapping("/enrollment")
    @Transactional
    public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
        System.out.println("GradeBook addEnrollment " + enrollmentDTO);

        // Create a new Enrollment object based on the received EnrollmentDTO
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentEmail(enrollmentDTO.studentEmail());
        enrollment.setStudentName(enrollmentDTO.studentName());
        
        // You should look up the course from the repository based on courseId
        Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
        if (course != null) {
            enrollment.setCourse(course);
            enrollment = enrollmentRepository.save(enrollment);

            // Convert the saved enrollment back to EnrollmentDTO and return it
            EnrollmentDTO savedEnrollmentDTO = new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getStudentEmail(),
                enrollment.getStudentName(),
                enrollment.getCourse().getCourse_id()
            );

            return savedEnrollmentDTO;
        } else {
            // Handle the case where the course with the given courseId is not found.
            // You can return an appropriate response or throw an exception.
            return null;
        }
    }

    @Override
    public void sendFinalGrades(int course, CourseDTOG courseDTO) {
        // TODO: Implement sending final grades for a specific course using the courseDTO.
    }
}
