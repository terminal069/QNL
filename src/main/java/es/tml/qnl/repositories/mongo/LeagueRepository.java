package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import es.tml.qnl.model.mongo.League;

public interface LeagueRepository extends MongoRepository<League, String> {
	
	
}
