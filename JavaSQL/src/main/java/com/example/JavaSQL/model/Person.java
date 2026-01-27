package com.example.JavaSQL.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "person")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_person;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false, name = "id_address")
	private Address id_address;

	@Column(nullable = false)
	private String last_name;

	@CreatedDate
	@Column(updatable = false)
	private Instant creation_date;

	@LastModifiedDate
	private Instant modification_date;
}

// create table Person(
// id_person bigint primary key generated always as identity,
// id_address bigint references Address(id_address),
// last_name varchar(255) not null,
// creation_date timestamp DEFAULT current_timestamp,
// modification_date timestamp DEFAULT current_timestamp
// );