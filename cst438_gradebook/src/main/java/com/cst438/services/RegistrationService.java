package com.cst438.services;

import com.cst438.dto.CourseDTOG;
import com.cst438.dto.FinalGradeDTO;

public interface RegistrationService {
	
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades);

	void sendFinalGrades(int course, CourseDTOG courseDTO);

}