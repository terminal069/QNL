package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Season;

public interface SeasonRepository extends MongoRepository<Season, String> {

	@Query("{ 'leagueCode' : ?0 }")
	List<Season> findByLeague(String leagueCode);
}
