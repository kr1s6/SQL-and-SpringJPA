package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = """
					INSERT INTO student(student_number, id_person) VALUES
					(234223, 1), (22222, 2), (333333, 3), (444444, 4)
					""")
	void initStudent();
}
