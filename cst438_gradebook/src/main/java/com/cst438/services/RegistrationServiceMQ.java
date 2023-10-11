package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.FinalGradeDTO;


public class RegistrationServiceMQ implements RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		rabbitTemplate.convertAndSend(registrationQueue.getName(), enrollmentDTO);
		
		//Receive message for new enrollment and add enrollment to an existing course
		
		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
		
		if( course == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Course not found."+enrollmentDTO.courseId());
		}
		
		Enrollment enrollment = new Enrollment();
		enrollment.setStudentName(enrollmentDTO.studentName());
		enrollment.setStudentEmail(enrollmentDTO.studentEmail());
		enrollment.setCourse(course);
		enrollment = enrollmentRepository.save(enrollment);

		
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course, CourseDTOG courseDTO) {
		//Send message of final grades to registration back-end
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);	
	}

}