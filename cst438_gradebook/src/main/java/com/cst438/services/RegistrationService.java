package com.cst438.services;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.FinalGradeDTO;

public interface RegistrationService {
	
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades);

	void sendFinalGrades(int course, CourseDTOG courseDTO);

}