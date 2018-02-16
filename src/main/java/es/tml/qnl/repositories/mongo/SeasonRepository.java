package es.tml.qnl.repositories.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import es.tml.qnl.model.mongo.Season;

public interface SeasonRepository extends MongoRepository<Season, String> {

	@Query("{ 'leagueCode': ?0, 'currentSeason': false , $and: [ {'code': {$gte: ?1}}, {'code': {$lte: ?2}} ] }")
	List<Season> findByLeagueAndNotCurrentSeasonAndSeasonCodeRank(String leagueCode, int seasonGreaterThanOrEqual, int seasonLessThanOrEqual);

	@Query("{ 'leagueCode': ?0, 'code': ?1 }")
	Season findByLeagueAndSeason(String leagueCode, int seasonCode);
}
