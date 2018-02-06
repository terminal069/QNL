package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPointsPosition;

public interface StatPointsPositionRepository extends MongoRepository<StatPointsPosition, String> {

	@Query(value = "{ 'points': ?0, 'position': ?1 }")
	StatPointsPosition findByPointsAndPosition(int points, int position);
}
