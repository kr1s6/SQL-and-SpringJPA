package com.example.repository;

import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Transactional(readOnly = true)
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<List<Student>> findByFirstName(String name);
	Optional<List<Student>> findByFirstNameContaining(String name);
	Optional<List<Student>> findByLastNameNotNull();
	List<Student> findByGuardianName(String name);

	@Query("select s from Student s where s.emailId = ?1")
	Student getStudentByEmailAddress(String emailId);
	//$1 - first parameter

	@Query("select s.firstName from Student s where s.emailId = ?1")
	String getStudentFirstNameByEmailAddress(String emailId);


	@Query(
			value = "SELECT * FROM student s where s.email_address = ?1",
			nativeQuery = true
	)
	Student getStudentByEmailAddressNative(String emailId);


	//Native Named Param
	@Query(
			value = "SELECT * FROM student s where s.email_address = :emailId",
			nativeQuery = true
	)
	Student getStudentByEmailAddressNativeNamedParam(@Param("emailId") String emailId);


	@Modifying
	@Transactional
	@Query(
			value = "update student set first_name = ?1 where email_address = ?2",
			nativeQuery = true
	)
	int updateStudentNameByEmailId(String firstName, String emailId);

}
