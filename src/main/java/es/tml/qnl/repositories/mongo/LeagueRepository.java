package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.League;

public interface LeagueRepository extends MongoRepository<League, String> {

	@Query("{ 'code' : ?0 }")
	League findByLeague(String leagueCode);
	
}
