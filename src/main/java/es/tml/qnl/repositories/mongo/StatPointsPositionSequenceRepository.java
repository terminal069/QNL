package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPointsPositionSequence;

public interface StatPointsPositionSequenceRepository extends MongoRepository<StatPointsPositionSequence, String> {

	@Query(value = "{ 'points': ?0, 'position': ?1, 'sequence': ?2 }")
	StatPointsPositionSequence findByPointsAndPositionAndSequence(int points, int position, String sequence);
}
