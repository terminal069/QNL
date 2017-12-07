package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.StatResultSequence;

public interface StatResultSequenceRepository extends MongoRepository<StatResultSequence, String> {

	
}
