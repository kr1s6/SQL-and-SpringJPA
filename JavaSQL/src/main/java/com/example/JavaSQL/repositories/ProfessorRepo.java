package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProfessorRepo extends JpaRepository<Professor, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = """
					INSERT INTO professor(professor_number, id_person) VALUES
					(121212, 1), (555555, 5)
					""")
	void initProfessor();
}
