package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.RoundPrediction;

public interface RoundPredictionRepository extends MongoRepository<RoundPrediction, String> {

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, $or: [ {'local': ?3}, {'visitor': ?3} ] }")
	RoundPrediction findbyLeagueAndSeasonAndRoundAndTeam(String leagueCode, int seasonCode, int roundNumber, String team);
	
	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, 'local': ?3, 'visitor': ?4 }")
	RoundPrediction findByLeagueAndSeasonAndRoundAndLocalAndVisitor(String league, int season, int round,
			String local, String visitor);

}
