package com.example.JavaSQL.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "country")
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_country;

	@Column(unique = true, nullable = false)
	private String name;
}


//create table Country(
//	id_country bigint PRIMARY KEY generate always as identity,
//	name varchar(255) unique not null
//);

