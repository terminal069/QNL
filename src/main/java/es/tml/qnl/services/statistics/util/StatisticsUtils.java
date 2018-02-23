package es.tml.qnl.services.statistics.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.tml.qnl.model.mongo.GenericRound;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.util.enums.Result;

@Component
public class StatisticsUtils {
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;

	/**
	 * Gets difference of points before the match
	 * 
	 * @param round Round data
	 * @return Difference of points
	 */
	public int getPointsBeforeMatch(GenericRound round) {

		int difference;
		int localPoints = round.getLocalPoints();
		int visitorPoints = round.getVisitorPoints();
		
		if (round.getLocalRes() - round.getVisitorRes() > 0) {
			difference = localPoints - visitorPoints - win;
		}
		else if (round.getLocalRes() == round.getVisitorRes()) {
			difference = localPoints - visitorPoints;
		}
		else {
			difference = localPoints - visitorPoints + win;
		}
		
		return difference;
	}
	
	/**
	 * Calculate difference of position of two teams, before the round
	 * 
	 * @param round Round
	 * @return Difference of position of the previous round
	 */
	public Integer getPositionBeforeMatch(GenericRound round) {
		
		Integer previousPositionDifference = null;
		int roundNumber = round.getRoundNumber();
		
		if (roundNumber > 1) {
			
			String leagueCode = round.getLeagueCode();
			int seasonCode = round.getSeasonCode();
			String local = round.getLocal();
			String visitor = round.getVisitor();
			GenericRound previousLocalRound;
			GenericRound previousVisitorRound;
			
			if (round instanceof Round) {
				previousLocalRound = roundRepository.findbyLeagueAndSeasonAndRoundAndTeam(
						leagueCode,
						seasonCode,
						roundNumber - 1,
						local);
				
				previousVisitorRound = roundRepository.findbyLeagueAndSeasonAndRoundAndTeam(
						leagueCode,
						seasonCode,
						roundNumber - 1,
						visitor);
			}
			else {
				previousLocalRound = roundPredictionRepository.findbyLeagueAndSeasonAndRoundAndTeam(
						leagueCode,
						seasonCode,
						roundNumber - 1,
						local);
				
				previousVisitorRound = roundPredictionRepository.findbyLeagueAndSeasonAndRoundAndTeam(
						leagueCode,
						seasonCode,
						roundNumber - 1,
						visitor);
			}
			
			if (previousLocalRound != null && previousVisitorRound != null) {
				
				int previousLocalPosition = previousLocalRound.getLocal().equals(local) ? 
						previousLocalRound.getLocalPosition() : previousLocalRound.getVisitorPosition();
				int previousVisitorPosition = previousVisitorRound.getLocal().equals(visitor) ?
						previousVisitorRound.getLocalPosition() : previousVisitorRound.getVisitorPosition();
						
				previousPositionDifference = previousLocalPosition - previousVisitorPosition;
			}
		}
		
		return previousPositionDifference;
	}
	
	/**
	 * Calculates result for a team and a round. Indicates if a team has won, drawn or lost
	 * in a round 
	 * 
	 * @param name Team name
	 * @param round Round data
	 * @return Result (win, draw or lose)
	 */
	public Result calculateResult(String name, GenericRound round) {
		
		Result result = null;
		
		if (round.getLocal().equals(name)) {
			result = round.getLocalRes() > round.getVisitorRes() ? Result.A :
				round.getLocalRes() == round.getVisitorRes() ? Result.B : Result.C;
		}
		else {
			result = round.getVisitorRes() > round.getLocalRes() ? Result.A :
				round.getVisitorRes() == round.getLocalRes() ? Result.B : Result.C;
		}
		
		return result;
	}
}
