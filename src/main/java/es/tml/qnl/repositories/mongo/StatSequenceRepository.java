package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatSequence;

public interface StatSequenceRepository extends MongoRepository<StatSequence, String> {

	@Query(value = "{ 'sequence': ?0 }")
	StatSequence findBySequence(String sequence);
	
}
