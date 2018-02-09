package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Round;

public interface RoundRepository extends MongoRepository<Round, String> {

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1 }", delete = true)
	void deleteByLeagueAndSeason(String leagueCode, int seasonCode);

	@Query("{ $and: [" 
			+ "  ?#{ [0] == null ? { $where: 'true' } : { 'roundNumber': [0] }},"
			+ "  ?#{ [1] == null ? { $where: 'true' } : { 'seasonCode': [1] }},"
			+ "  ?#{ [2] == null ? { $where: 'true' } : { 'leagueCode': [2] }},"
			+ "  ?#{ [3] == null ? { $where: 'true' } : { 'local': [3] }},"
			+ "  ?#{ [4] == null ? { $where: 'true' } : { 'visitor': [4] }}"
			+ "]}")
	List<Round> findByRoundAndSeasonAndLeagueAndLocalAndVisitor(Integer roundNumber, Integer seasonCode,
			String leagueCode, String local, String visitor);
	
	@Query(value = "{ 'local': ?0, 'visitor': ?1 }")
	List<Round> findByLocalAndVisitor(String local, String visitor);
	
	@Query(value = "{ $or: [ {'local': ?0}, {'visitor': ?0} ] }")
	List<Round> findByTeamSorted(String team, Sort sort);
	
	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1 }")
	List<Round> findByLeagueAndSeasonSorted(String leagueCode, int seasonCode, Sort sort);

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, $or: [ {'local': ?3}, {'visitor': ?3} ] }")
	Round findbyLeagueAndSeasonAndRoundAndTeam(String leagueCode, int seasonCode, int roundNumber, String team);

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1, 'roundNumber': ?2, 'local': ?3, 'visitor': ?4 }")
	Round findByLeagueAndSeasonAndRoundAndLocalAndVisitor(String league, int season, int round,
			String local, String visitor);
}
