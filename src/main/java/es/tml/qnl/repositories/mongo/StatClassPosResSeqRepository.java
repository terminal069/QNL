package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatClassPosResSeq;

public interface StatClassPosResSeqRepository extends MongoRepository<StatClassPosResSeq, String> {

	@Query(value = "{ 'positionDifference': ?0, 'sequence': ?1 }")
	StatClassPosResSeq findByPositionDifferenceAndSequence(Integer positionDifference, String sequence);
	
}
