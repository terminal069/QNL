package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.Round;

public interface RoundRepository extends MongoRepository<Round, String> {

}
