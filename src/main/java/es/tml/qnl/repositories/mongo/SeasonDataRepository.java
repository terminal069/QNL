package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.SeasonData;

public interface SeasonDataRepository extends MongoRepository<SeasonData, String> {

}
