package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatDifferenceOfPoints;

public interface StatDifferenceOfPointsRepository extends MongoRepository<StatDifferenceOfPoints, String> {

	@Query(value = "{ 'difference': ?0 }")
	StatDifferenceOfPoints getStatByDifference(int difference);

}
