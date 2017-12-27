package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.SeasonRange;

public interface SeasonRangeRepository extends MongoRepository<SeasonRange, String> {

}
