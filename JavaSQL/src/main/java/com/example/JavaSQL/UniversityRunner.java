package com.example.JavaSQL;

import com.example.JavaSQL.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
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

		Instant from = LocalDate.parse("2026-01-01").atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant to = LocalDate.parse("2026-01-31").atStartOfDay().toInstant(ZoneOffset.UTC);

		List<Object[]> modificated_users_values = personRepo.modificated_users(from, to);
		for(Object[] row : modificated_users_values) {
			String last_name = (String) row[0];
			Instant modification_date = (Instant) row[1];
			Integer student_number = row[2] == null ? null : (Integer) row[2];
			Integer professor_number = row[3] == null ? null : (Integer) row[3];
			String address = (String) row[4];

			String result = "last_name=" + last_name +
					", modification_date=" + modification_date +
					", student_number=" + student_number +
					", professor_number=" + professor_number +
					", address=" + address;

			System.out.println(result);
		}


		System.out.println("*********************************************************************************");

		List<Object[]> groupPersonByCountry = personRepo.groupPersonByCountry("England");
		for(Object[] row : groupPersonByCountry) {
			String country = (String) row[0];
			String last_name = (String) row[1];
			String person_type = (String) row[2];

			String result = "country=" + country +
					", last_name=" + last_name +
					", person_type=" + person_type;
			System.out.println(result);
		}



	}
}
