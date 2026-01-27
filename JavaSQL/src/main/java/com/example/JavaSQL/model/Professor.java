package com.example.JavaSQL.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "professor")
public class Professor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_professor;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false, name = "id_person")
	private Person id_person;

	@Column(unique = true, nullable = false)
	private int professor_number;
}

// create table Professor(
// id_professor bigint primary key generate always as identity,
// id_person bigint references Person(id_person),
// professor_number bigint unique not null
// );