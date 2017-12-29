package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.CurrentRound;

public interface CurrentRoundRepository extends MongoRepository<CurrentRound, String> {

}
