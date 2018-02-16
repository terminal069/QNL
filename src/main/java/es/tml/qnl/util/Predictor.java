package es.tml.qnl.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.model.mongo.RoundPrediction;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.services.statistics.util.StatisticsType;
import es.tml.qnl.services.statistics.util.StatisticsType.StatisticType;
import es.tml.qnl.services.statistics.util.StatisticsUtils;
import es.tml.qnl.util.enums.Result;

/**
 * Component used to make predictions about the results of matches
 * 
 * @author jcerrato
 *
 */
@Component
public class Predictor {
	
	private static final String QNL_DEFAULT_MAX_ITERATIONS = "qnl.defaultMaxIterations";
	private static final String QNL_STATISTICS_MIN_DATA_QUANTITY_TO_BE_VALID = "qnl.statistics.minDataQuantityToBeValid";
	
	@Value("${" + QNL_DEFAULT_MAX_ITERATIONS + "}")
	private Integer qnlDefaultMaxIterations;
	
	@Value("${" + QNL_STATISTICS_MIN_DATA_QUANTITY_TO_BE_VALID + "}")
	private Integer minDataQuantityToBeValid;
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private StatisticsUtils statisticsUtils;
	
	@Autowired
	private StatisticsType statisticsType;

	/**
	 * Predict results from a list of matches
	 * 
	 * @param matches List of matches
	 * @return List of predictions
	 */
	public List<Prediction> predict(List<Match> matches) {

		List<Prediction> predictions = new ArrayList<>();
		
		matches.forEach(match -> predictions.add(predict(match)));
		
		return predictions;
	}

	/**
	 * Predict result from a match
	 * 
	 * @param match Match
	 * @return Prediction
	 */
	public Prediction predict(Match match) {

		// Get round
		RoundPrediction round = getRound(match);
		
		if (round == null) {
			round = getRoundFromMatch(match);
		}
		
		return preparePrediction(round);
	}

	/**
	 * Transform match data into round data
	 * 
	 * @param match Match data
	 * @return Round data
	 */
	private RoundPrediction getRoundFromMatch(Match match) {

		RoundPrediction localRound = roundPredictionRepository.findbyLeagueAndSeasonAndRoundAndTeam(
				match.getLeague(),
				match.getSeason(),
				match.getRound() - 1,
				match.getLocal());
		
		RoundPrediction visitorRound = roundPredictionRepository.findbyLeagueAndSeasonAndRoundAndTeam(
				match.getLeague(),
				match.getSeason(),
				match.getRound() - 1,
				match.getVisitor());
		
		return new RoundPrediction(
				match.getRound(),
				match.getSeason(),
				match.getLeague(),
				match.getLocal(),
				match.getVisitor(),
				0,
				0,
				localRound.getLocal().equals(match.getLocal()) ?
						localRound.getLocalPoints() : localRound.getVisitorPoints(),
				visitorRound.getVisitor().equals(match.getVisitor()) ?
						visitorRound.getVisitorPoints() : visitorRound.getLocalPoints());
	}

	/**
	 * Search for a round in the round prediction repository from the data of a match
	 * 
	 * @param match Match data
	 * @return A round, or {@code null} if no round is found
	 */
	private RoundPrediction getRound(Match match) {
		
		return Optional.ofNullable(roundPredictionRepository.findByLeagueAndSeasonAndRoundAndLocalAndVisitor(
				match.getLeague(),
				match.getSeason(),
				match.getRound(),
				match.getLocal(),
				match.getVisitor()))
			.orElse(null);
	}
	
	/**
	 * Prepare the calculation and estimation of the results of a match from the data of a round
	 * 
	 * @param round Round data
	 * @return The prediction
	 */
	private Prediction preparePrediction(RoundPrediction round) {
		
		// Get points, position and sequence from the match
		int points = statisticsUtils.getPointsBeforeMatch(round);
		Integer position = statisticsUtils.getPositionBeforeMatch(round);
		
		int sequenceSize = round.getRoundNumber() <= qnlDefaultMaxIterations ?
				round.getRoundNumber() - 1 : qnlDefaultMaxIterations;
		String localSequence = getSequence(round, round.getLocal(), sequenceSize);
		String visitorSequence = getSequence(round, round.getVisitor(), sequenceSize);
		
		// Get all types of statistics from the points, position and sequence calculated previously
		List<StatsModelBase> localStats = statisticsType.getStatistic(StatisticType.ALL, points, position, localSequence);
		List<StatsModelBase> visitorStats = statisticsType.getStatistic(StatisticType.ALL, points, position, visitorSequence);
		
		// Calculate predictions
		Prediction localPrediction = calculatePrediction(localStats);
		Prediction visitorPrediction = calculatePrediction(visitorStats);
		
		// TODO: estimate result with predictions calculated before
			
		
		
		
		return null;
	}

	/**
	 * Get a sequence of {@code sequenceSize} result elements for a team
	 * 
	 * @param round Round
	 * @param team Team
	 * @param sequenceSize Size of the sequence
	 * @return Sequence of results
	 */
	private String getSequence(RoundPrediction round, String team, int sequenceSize) {
		
		fifoQueue.clear();
		fifoQueue.setSize(sequenceSize);
		
		roundPredictionRepository.findByLeagueAndSeasonAndTeamFromRoundToRoundSorted(
				round.getLeagueCode(),
				round.getSeasonCode(),
				team,
				round.getRoundNumber() - sequenceSize,
				round.getRoundNumber(),
				new Sort("roundNumber"))
			.stream()
			.forEach(r -> fifoQueue.push(statisticsUtils.calculateResult(team, r)));
		
		return fifoQueue.toStringFromHeadToTail();
	}
	
	/**
	 * Calculates a prediction from a list of statistics
	 * 
	 * @param stats List of statistics
	 * @return Calculated prediction
	 */
	private Prediction calculatePrediction(List<StatsModelBase> stats) {

		Prediction prediction = new Prediction();
		
		List<StatsModelBase> statsFiltered = stats.stream()
				.filter(localStat -> isStatValid(localStat))
				.collect(Collectors.toList());
		
		BigDecimal increasePercentage = calculateIncreasePercentage(statsFiltered);
		
		return statsFiltered.stream()
				.map(stat -> calculatePredictionWithIncreasePercentage(stat, increasePercentage))
				.reduce(prediction, this::addPrediction);
	}
	
	/**
	 * Check if a statistic is valid based on different criterias:
	 * <ul>
	 *   <li>Minimum number of elements</li>
	 * </ul>
	 * 
	 * @return {@code true} if statistic is valid, {@code false} otherwise
	 */
	private boolean isStatValid(StatsModelBase stat) {
		
		return (stat.getLocalWinner() + stat.getTied() + stat.getVisitorWinner()) >= minDataQuantityToBeValid ?
				true : false;
	}
	
	/**
	 * Calculate the percentage to increase to the weight of each statistic. This is because
	 * the total weight of all statistics must sum 1.0. If not all statistict are used, is
	 * needed to recalculate each weight multiplying it with an increment to get a sumo of 1.0.
	 * This increment is calculated adding the weights of used statistics and calculating its
	 * inverse function
	 * 
	 * @param stats List of statistics
	 * @return Percentage to increase
	 */
	private BigDecimal calculateIncreasePercentage(List<StatsModelBase> stats) {

		BigDecimal increasePercentage = BigDecimal.ZERO;
		
		stats.forEach(stat -> {
			increasePercentage.add(statisticsType.getStatisticWeigth(stat));
		});
		
		return BigDecimal.ONE.divide(increasePercentage);
	}
	
	/**
	 * Calculate a prediction from statistic data increasing the percentage used
	 * 
	 * @param stat Statistic data
	 * @param increasePercentage Percentage to increase
	 * @return The prediction
	 */
	private Prediction calculatePredictionWithIncreasePercentage(StatsModelBase stat, BigDecimal increasePercentage) {
		
		BigDecimal total = new BigDecimal(stat.getLocalWinner())
				.add(new BigDecimal(stat.getTied()))
				.add(new BigDecimal(stat.getVisitorWinner()));
		
		return new Prediction(
				null,
				null,
				null,
				new BigDecimal(stat.getLocalWinner()).divide(total).multiply(increasePercentage),
				new BigDecimal(stat.getTied()).divide(total).multiply(increasePercentage),
				new BigDecimal(stat.getVisitorWinner()).divide(total).multiply(increasePercentage));
	}
	
	/**
	 * Add data prediction to a previously calculated prediction
	 * 
	 * @param basePrediction Previously calculated prediction
	 * @param prediction Prediction to add
	 * @return Prediction with the combination of the predictions
	 */
	private Prediction addPrediction(Prediction basePrediction, Prediction prediction) {
		
		basePrediction.setLocalWinProbability(
				basePrediction.getLocalWinProbability().add(prediction.getLocalWinProbability()));
		basePrediction.setDrawProbability(
				basePrediction.getDrawProbability().add(prediction.getDrawProbability()));
		basePrediction.setVisitorWinProbability(
				basePrediction.getVisitorWinProbability().add(prediction.getVisitorWinProbability()));
		
		return basePrediction;
	}

}
