package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPositionPointsSequence;

public interface StatPositionPointsSequenceRepository extends MongoRepository<StatPositionPointsSequence, String> {

	@Query(value = "{ 'position': ?0, 'points': ?1, 'sequence': ?2 }")
	StatPositionPointsSequence findByPositionAndPointsAndSequence(int position, int points, String sequence);
}
