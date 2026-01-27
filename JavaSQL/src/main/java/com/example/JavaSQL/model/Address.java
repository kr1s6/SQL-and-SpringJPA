package com.example.JavaSQL.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "address")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_address;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false, name = "id_country")
	private Country id_country;

	@Column(nullable = false)
	private String street;

	@Column(nullable = false)
	private String city;
}

// create table Address(
// id_address bigint primary key generate always as identity,
// id_country bigint references Country(id_country),
// street varchar(255) not null
// city varchar(255) not null
// );
