package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = """
					INSERT INTO person(id_address, last_name, creation_date, modification_date) VALUES
					(1, 'Kowalski', now(), now()),
					(1, 'Nowak', now(), now()),
					(2, 'Smesny', now(), now()),
					(3, 'Nsmesny', now(), now()),
					(4, 'Inny', now(), now()),
					(4, 'Beziczego', now(), now())
					""")
	void initPerson();
	//JPA Auditing `@CreatedDate` `@ModificationDate` isn't working for native queries


	@Query(value = """
			SELECT COUNT(s) as students, COUNT(pr) as professors, c.name as country
			FROM Person p
			LEFT JOIN Student s ON p = s.id_person
			LEFT JOIN Professor pr ON p = pr.id_person
			JOIN Address a ON p.id_address = a
			JOIN Country c ON a.id_country = c
			GROUP BY c.name
			HAVING COUNT(s) + COUNT(pr) > 0
			""")
	List<Object[]> students_and_professors_grouped_by_country();


	@Query(value = """
			SELECT p.last_name, p.modification_date, s.student_number, pr.professor_number, CONCAT(a.city, ', ', a.street) AS Address
			FROM Person p
			LEFT JOIN Student s ON s.id_person = p
			LEFT JOIN Professor pr ON pr.id_person = p
			JOIN Address a ON p.id_address = a
			WHERE p.modification_date BETWEEN :from AND :to
			""")
	List<Object[]> modificated_users(@Param("from") Instant from, @Param("to") Instant to);


	@Query(value = """
			SELECT c.name as Country, p.last_name, CASE
			  WHEN s.id_person IS NOT NULL THEN 'STUDENT'
			  WHEN pr.id_person IS NOT NULL THEN 'PROFESSOR'
			  ELSE 'UNKNOWN'
			  END AS person_type
			  FROM Person p
			  LEFT JOIN Student s ON s.id_person = p
			  LEFT JOIN Professor pr ON pr.id_person = p
			  JOIN Address a ON p.id_address = a
			  JOIN Country c ON a.id_country = c
			  WHERE c.name = :country
			""")
	List<Object[]> groupPersonByCountry(@Param("country") String country);

}
