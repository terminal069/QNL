package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.RoundPrediction;

public interface RoundPredictionRepository extends MongoRepository<RoundPrediction, String> {

}
