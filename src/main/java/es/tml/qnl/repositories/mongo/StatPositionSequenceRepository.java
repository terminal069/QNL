package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPositionSequence;

public interface StatPositionSequenceRepository extends MongoRepository<StatPositionSequence, String> {

	@Query(value = "{ 'position': ?0, 'sequence': ?1 }")
	StatPositionSequence findByPositionAndSequence(Integer position, String sequence);
	
}
