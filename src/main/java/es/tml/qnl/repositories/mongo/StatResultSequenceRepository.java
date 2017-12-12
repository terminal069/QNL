package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatResultSequence;

public interface StatResultSequenceRepository extends MongoRepository<StatResultSequence, String> {

	@Query(value = "{ 'sequence': ?0 }")
	StatResultSequence findBySequence(String sequence);
	
}
