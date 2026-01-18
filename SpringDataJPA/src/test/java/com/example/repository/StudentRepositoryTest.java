package com.example.repository;

import com.example.model.embeddable.Guardian;
import com.example.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class StudentRepositoryTest {

	@Autowired
	StudentRepository studentRepository;

	@Test
	@DisplayName("Find student by id")
	void givenStudentID_whenQuery_thenGetStudentObject() {
		//Given
		studentRepository.save(new Student("Adam", "Grant", "Email", new Guardian("SSSS", "SSSS", "SSSS")));
		Student student = studentRepository.getReferenceById(1L);
	}
}
