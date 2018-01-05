package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatClassificationPosition;

public interface StatClassificationPositionRepository extends MongoRepository<StatClassificationPosition, String> {

	@Query(value = "{ 'positionDifference': ?0 }")
	StatClassificationPosition findByPositionDifference(Integer positionDifference);
}
