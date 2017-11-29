package es.tml.qnl.repositories.mongo;

import java.util.List;

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
	List<Round> getRoundByRoundSeasonLeagueLocalVisitor(Integer roundNumber, Integer seasonCode,
			String leagueCode, String local, String visitor);
}
