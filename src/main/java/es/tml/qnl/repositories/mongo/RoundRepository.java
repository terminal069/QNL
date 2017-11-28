package es.tml.qnl.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Round;

public interface RoundRepository extends MongoRepository<Round, String> {

	@Query(value = "{ 'leagueCode': ?0, 'seasonCode': ?1 }", delete = true)
	void deleteByLeagueAndSeason(String leagueCode, int seasonCode);

}
