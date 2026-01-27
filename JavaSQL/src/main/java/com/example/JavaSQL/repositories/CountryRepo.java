package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Country;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {

	@PostConstruct
	@Modifying
	@Transactional
	@Query(nativeQuery = true,
			value = """ 
					INSERT INTO country(name) VALUES
					('Poland'), ('Germany'), ('England')
					"""
	)
	void initialCountries();
}
