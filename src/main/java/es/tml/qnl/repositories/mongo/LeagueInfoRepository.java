package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.LeagueInfo;

public interface LeagueInfoRepository extends MongoRepository<LeagueInfo, String> {

	@Query("{ 'code' : ?0 }")
	LeagueInfo findByLeague(String leagueCode);
	
}
