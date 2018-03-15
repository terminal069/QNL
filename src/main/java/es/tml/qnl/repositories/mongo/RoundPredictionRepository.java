package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.GenericRound;
import es.tml.qnl.model.mongo.RoundPrediction;

public interface RoundPredictionRepository extends MongoRepository<RoundPrediction, String> {

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, $or: [ {'local': ?3}, {'visitor': ?3} ] }")
	RoundPrediction findbyLeagueAndSeasonAndRoundAndTeam(String leagueCode, int seasonCode, int roundNumber, String team);
	
	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, 'local': ?3, 'visitor': ?4 }")
	RoundPrediction findByLeagueAndSeasonAndRoundAndLocalAndVisitor(String league, int season, int round,
			String local, String visitor);
	
	@Query(value = "{"
			+ "  'leagueCode': ?0,"
			+ "  'seasonCode': ?1,"
			+ "  $or: ["
			+ "    {'local': ?2},"
			+ "    {'visitor': ?2}"
			+ "  ],"
			+ "  $and: ["
			+ "    {'roundNumber': {$gte: ?3}},"
			+ "    {'roundNumber': {$lt: ?4}}"
			+ "  ]}")
	List<GenericRound> findByLeagueAndSeasonAndTeamFromRoundToRoundSorted(String league, int season, String team,
			int fromRound, int toRound, Sort sort);

}
