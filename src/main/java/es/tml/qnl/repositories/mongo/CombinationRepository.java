package es.tml.qnl.repositories.mongo;

import java.math.BigDecimal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Combination;

public interface CombinationRepository extends MongoRepository<Combination, String> {

	@Query(value = "{ 'interval': ?0, 'totalStats': ?1 }", delete = true)
	void deleteByIntervalAndTotalStats(BigDecimal interval, int totalStats);
}
