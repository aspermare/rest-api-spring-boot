package com.example.demo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntNumRepository extends CrudRepository<IntNum, Long> {
 
	IntNum findById(long id);
}
