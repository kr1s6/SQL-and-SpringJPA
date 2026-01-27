package com.example.JavaSQL.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student")
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_student;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false, name = "id_person")
	private Person id_person;

	@Column(unique = true, nullable = false)
	private int student_number;
}


//create table Student(
// id_student bigint primary key generate always as identity,
// id_person bigint references Person(id_person),
// student_number bigint unique not null;
// );