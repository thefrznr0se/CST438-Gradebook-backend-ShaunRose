package com.cst438.domain;

public record AssignmentDTO(int id, String assignmentName, String dueDate, String courseTitle, int courseId) {

	public static Assignment convertToEntity(AssignmentDTO adto, Course course) {
		// TODO Auto-generated method stub
		return null;
	}
}