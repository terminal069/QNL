package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatPosition;

public interface StatPositionRepository extends MongoRepository<StatPosition, String> {

	@Query(value = "{ 'position': ?0 }")
	StatPosition findByPosition(Integer position);
}
