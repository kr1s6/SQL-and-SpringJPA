package com.example.JavaSQL.repositories;

import com.example.JavaSQL.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

	@Transactional
	@Modifying
	@Query(nativeQuery = true,
			value = """
					INSERT INTO address(id_country, city, street) VALUES
					(1, 'Kraków', 'Karmelicka 2'), (1, 'Warszawa', 'Przemieścia 5'),
					(2, 'Berlin', 'Schemteling 10'), (3, 'Londyn', 'Big 10')
					"""
	)
	void initAddress();
}
