package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.Season;

public interface SeasonRepository extends MongoRepository<Season, String> {
	
	
}
