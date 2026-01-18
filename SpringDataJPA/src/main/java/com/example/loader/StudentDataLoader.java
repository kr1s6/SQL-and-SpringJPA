package com.example.loader;

import com.example.model.Student;
import com.example.model.embeddable.Guardian;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentDataLoader implements CommandLineRunner {

	private final StudentRepository studentRepository;

	@Override
	public void run(String... args) throws Exception {
		//studentRepository.save(Student.builder()
		//		.firstName("AdamGrant")
		//		.lastName("GrantHHAHAH")
		//		.emailId("Email")
		//		.guardian(new Guardian("SSSS", "SSSS", "SSSS"))
		//		.build()
		//);
		//studentRepository.save(Student.builder()
		//		.firstName("Edwart")
		//		.lastName(null)
		//		.emailId("EmailDDDDD")
		//		.guardian(new Guardian("XXX", "XXXX", "XXXXXXXXXXXXXX"))
		//		.build()
		//);
		//
		//
		//studentRepository.updateStudentNameByEmailId("EdwartChanged", "EmailDDDDD");
		//System.out.println(studentRepository.findByFirstName("EdwartChanged"));
	}
}
