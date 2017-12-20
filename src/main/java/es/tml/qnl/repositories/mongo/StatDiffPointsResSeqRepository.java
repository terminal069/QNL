package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.StatDiffPointsResSeq;

public interface StatDiffPointsResSeqRepository extends MongoRepository<StatDiffPointsResSeq, String> {

	@Query(value = "{ 'difference': ?0, 'sequence': ?1 }")
	StatDiffPointsResSeq findByDifferenceAndSequence(Integer difference, String sequence);
	
}
