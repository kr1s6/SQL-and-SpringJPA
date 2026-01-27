package com.example.JavaSQL;

import com.example.JavaSQL.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UniversityRunner implements CommandLineRunner {
	private final CountryRepo countryRepo;
	private final AddressRepo addressRepo;
	private final PersonRepo personRepo;
	private final ProfessorRepo professorRepo;
	private final StudentRepo studentRepo;

	@Override
	public void run(String... args) throws Exception {
		countryRepo.initialCountries();
		addressRepo.initAddress();
		personRepo.initPerson();
		studentRepo.initStudent();
		professorRepo.initProfessor();

		System.out.println("*********************************************************************************");

		List<Object[]> values = personRepo.students_and_professors_grouped_by_country();
		for(Object[] row : values) {
			Long students = (Long) row[0];
			Long professors = (Long) row[1];
			String country = (String) row[2];

			System.out.println(
				"Country: " + country +
				", students: " + students +
				", professors: " + professors
			);
		}

		System.out.println("*********************************************************************************");



	}
}
