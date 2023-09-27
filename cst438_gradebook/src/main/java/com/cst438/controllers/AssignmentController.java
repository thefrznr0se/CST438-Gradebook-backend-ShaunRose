package com.cst438.controllers;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	@GetMapping("/assignment/{id}")
	public ResponseEntity<AssignmentDTO> getAssignment(@PathVariable("id") int id) {
	    // Check that assignment belongs to the instructor
	    Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
	    if (assignmentOptional.isPresent()) {
	        Assignment assignment = assignmentOptional.get();
	        
	        // Replace the condition below with your instructor check logic.
	        if (isAssignmentInstructor(assignment, "dwisneski@csumb.edu")) {
	            AssignmentDTO dto = new AssignmentDTO(
	                assignment.getId(),
	                assignment.getName(),
	                assignment.getDueDate().toString(),
	                assignment.getCourse().getTitle(),
	                assignment.getCourse().getCourse_id());
	            return ResponseEntity.ok(dto);
	        } else {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assignment does not belong to the instructor");
	        }
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
	    }
	}

	@PostMapping("/assignment")
	public ResponseEntity<Integer> createAssignment(@RequestBody AssignmentDTO adto) {
	    // Check that course id in AssignmentDTO exists
	    Optional<Course> courseOptional = courseRepository.findById(adto.courseId());

	    if (!courseOptional.isPresent()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid course ID");
	    }

	    Course course = courseOptional.get();

	    // Check if the instructor is associated with the course
	    if ("dwizneski@csumb.edu".equals(course.getInstructor())) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Course does not belong to the instructor");
	    }

	    Assignment assignment = new Assignment();
	    assignment.setName(adto.assignmentName());

	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date dueDate = new Date(sdf.parse(adto.dueDate()).getTime());
	        assignment.setDueDate(dueDate);
	    } catch (ParseException e) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid due date format");
	    }

	    assignment.setCourse(course);
	    assignmentRepository.save(assignment);
	    return ResponseEntity.ok(assignment.getId());
	}


	@PutMapping("/assignment/{id}")
	public ResponseEntity<Void> updateAssignment(
	    @PathVariable("id") int id,
	    @RequestBody AssignmentDTO adto) {
	    
	    // Check if the assignment with the given 'id' exists
	    Assignment existingAssignment = assignmentRepository.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
	    
	    // Replace the condition below with your instructor check logic.
	    if (!isAssignmentInstructor(existingAssignment, "dwisneski@csumb.edu")) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assignment does not belong to the instructor");
	    }

	    // Update assignment with data in AssignmentDTO
	    existingAssignment.setName(adto.assignmentName());
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = null;
		try {
			date = (Date) sdf.parse(adto.dueDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    existingAssignment.setDueDate(date);
	    
	    // Save the updated assignment to the database
	    assignmentRepository.save(existingAssignment);
	    
	    return ResponseEntity.noContent().build();
	}


	@DeleteMapping("/assignment/{id}")
	public ResponseEntity<Void> deleteAssignment(
	        @PathVariable("id") int id,
	        @RequestParam("force") Optional<String> force) {
	    // Check if the assignment with the given 'id' exists
	    Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
	    if (assignmentOptional.isPresent()) {
	        Assignment assignment = assignmentOptional.get();
	        
	        if (isAssignmentInstructor(assignment, "dwisneski@csumb.edu")) {
	            // Check if there are grades for the assignment
	            if (hasGradesForAssignment(assignment)) {
	                // If there are grades and "force" is specified, delete the assignment
	                if (force.isPresent() && "true".equalsIgnoreCase(force.get())) {
	                    assignmentRepository.delete(assignment);
	                    return ResponseEntity.noContent().build();
	                } else {
	                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete assignment with grades. Use 'force=true' to override.");
	                }
	            } else {
	                // No grades, delete the assignment
	                assignmentRepository.delete(assignment);
	                return ResponseEntity.noContent().build();
	            }
	        } else {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assignment does not belong to the instructor");
	        }
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
	    }
	}

	// Helper methods for instructor and course validation
	private boolean isInstructorCourse(String instructorEmail, Course course) {
		return instructorEmail.equals(course.getInstructor());
	}

	private boolean isAssignmentInstructor(Assignment assignment, String instructorEmail) {
		return instructorEmail.equals(assignment.getCourse().getInstructor());
	}

	private boolean hasGradesForAssignment(Assignment assignment) {
	    Optional<Course> grades = courseRepository.findById(assignment.getId());

	    // Check if there are any grades associated with this assignment
	    return !grades.isEmpty();
	}
}
