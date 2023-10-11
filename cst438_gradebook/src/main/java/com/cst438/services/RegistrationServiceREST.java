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

    public RegistrationServiceREST() {
        System.out.println("REST registration service ");
    }

    @Override
    public void sendFinalGrades(int course_id , FinalGradeDTO[] grades) { 
        
        //TODO use restTemplate to send final grades to registration service
        System.out.println("Sending Final Grades: ");
        restTemplate.put(registration_url + "/course/" + course_id);
        
    }

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    
    /*
     * endpoint used by the registration service to add an enrollment to an existing
     * course.
     */
    @PostMapping("/enrollment")
    @Transactional
    public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
        System.out.println("GradeBook addEnrollment " + enrollmentDTO);

        // Create a new Enrollment object based on the received EnrollmentDTO
        Enrollment enrollment = new Enrollment();
        
        // Assuming that Enrollment has properties like studentEmail, studentName, courseId, and enrollmentDate
        // You should set these properties using the data from enrollmentDTO.
        enrollment.setStudentEmail(enrollmentDTO.studentEmail());
        enrollment.setStudentName(enrollmentDTO.studentName());
        enrollment.setCourseId(enrollmentDTO.courseId());

        // Save the new enrollment to the database
        enrollment = enrollmentRepository.save(enrollment);

        // Convert the saved enrollment back to EnrollmentDTO and return it
        EnrollmentDTO savedEnrollmentDTO = new EnrollmentDTO(
            enrollment.getId(),
            enrollment.getStudentEmail(),
            enrollment.getStudentName(),
            enrollment.getCourseId()
        );

        return savedEnrollmentDTO;
    }

    @Override
    public void sendFinalGrades(int course, CourseDTOG courseDTO) {
        // TODO Auto-generated method stub
        
    }
}
