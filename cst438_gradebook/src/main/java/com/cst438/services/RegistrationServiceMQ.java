package com.cst438.services;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.dto.CourseDTOG;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.FinalGradeDTO;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "mq")
@Configuration
public class RegistrationServiceMQ implements RegistrationService {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public RegistrationServiceMQ() {
        System.out.println("Initializing MQ registration service");
    }

    Queue registrationQueue = new Queue("registration-queue", true);

    @Bean
    public Queue createGradebookQueue() {
        return new Queue("gradebook-queue", true);
    }

    // Receive a message for a student added to a course
    @RabbitListener(queues = "gradebook-queue")
    @Transactional
    public void receive(String message) {
        System.out.println("Received a message from the Gradebook: " + message);

        // TODO: Deserialize the message to EnrollmentDTO and update the database
        EnrollmentDTO enrollmentDTO = fromJsonString(message, EnrollmentDTO.class);

        Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
        Enrollment newCourseEnrollment = new Enrollment();
        newCourseEnrollment.setCourse(course);
        newCourseEnrollment.setStudentName(enrollmentDTO.studentName());
        newCourseEnrollment.setStudentEmail(enrollmentDTO.studentEmail());
        enrollmentRepository.save(newCourseEnrollment);
    }

    // Send final grades to the Registration Service
    @Override
    public void sendFinalGrades(int courseId, FinalGradeDTO[] grades) {
        System.out.println("Starting to send final grades for course " + courseId);

        // TODO: Convert grades to a JSON string and send it to the registration service
        String data = asJsonString(grades);
        rabbitTemplate.convertAndSend(registrationQueue.getName(), data);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void sendFinalGrades(int course, CourseDTOG courseDTO) {
		// TODO Auto-generated method stub
		
	}
}
