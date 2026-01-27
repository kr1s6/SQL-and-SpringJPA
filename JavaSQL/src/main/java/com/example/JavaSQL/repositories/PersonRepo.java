package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = """
					INSERT INTO person(id_address, last_name) VALUES
					(1, 'Kowalski'), (1, 'Nowak'), (2, 'Smesny'), (3, 'Nsmesny'), (4, 'Inny'), (4, 'Beziczego')
					""")
	void initPerson();


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
}
