package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPointsSequence;

public interface StatPointsSequenceRepository extends MongoRepository<StatPointsSequence, String> {

	@Query(value = "{ 'points': ?0, 'sequence': ?1 }")
	StatPointsSequence findByPointsAndSequence(Integer points, String sequence);
	
}
