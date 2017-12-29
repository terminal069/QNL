package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Season;

public interface SeasonRepository extends MongoRepository<Season, String> {

	@Query("{ 'leagueCode': ?0, $and: [ {'code': {$gte: ?1}}, {'code': {$lte: ?2}} ] }")
	List<Season> findByLeagueAndSeasonCodeRank(String leagueCode, int greaterThanOrEqual, int lessThanOrEqual);

	@Query("{ 'leagueCode': ?0, 'code': ?1 }")
	Season findByLeagueAndSeason(String leagueCode, String seasonCode);
}
