package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPoints;

public interface StatPointsRepository extends MongoRepository<StatPoints, String> {

	@Query(value = "{ 'points': ?0 }")
	StatPoints findByPoints(Integer points);

}
