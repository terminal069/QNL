package es.tml.qnl.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.model.mongo.GenericRound;
import es.tml.qnl.model.mongo.RoundPrediction;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.services.statistics.util.StatisticsType;
import es.tml.qnl.services.statistics.util.StatisticsType.StatisticType;
import es.tml.qnl.services.statistics.util.StatisticsUtils;
import es.tml.qnl.util.enums.Result;
import lombok.Setter;

/**
 * Component used to make predictions about the results of matches
 * 
 * @author jcerrato
 *
 */
@Component
public class Predictor {
	
	private static final int BIG_DECIMAL_BASE_SCALE = 10;
	private static final BigDecimal BIG_DECIMAL_TWO = new BigDecimal(2);
	
	private static final String QNL_DEFAULT_MAX_ITERATIONS = "qnl.defaultMaxIterations";
	private static final String QNL_STATISTICS_MIN_DATA_QUANTITY_TO_BE_VALID = "qnl.statistics.minDataQuantityToBeValid";
	private static final String QNL_STATISTICS_MULTIPLE = "qnl.statistics.multiple";
	
	@Setter
	@Value("${" + QNL_DEFAULT_MAX_ITERATIONS + "}")
	private Integer qnlDefaultMaxIterations;
	
	@Value("${" + QNL_STATISTICS_MIN_DATA_QUANTITY_TO_BE_VALID + "}")
	private Integer minDataQuantityToBeValid;
	
	@Value("${" + QNL_STATISTICS_MULTIPLE + "}")
	private StatisticType statisticUsed;
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private StatisticsUtils statisticsUtils;
	
	@Setter
	@Autowired
	private StatisticsType statisticsType;
	
	@Setter
	private boolean isPrediction;

	/**
	 * Predict results from a list of matches
	 * 
	 * @param matches List of matches
	 * @return List of predictions
	 */
	public List<Prediction> predict(List<Match> matches) {

		List<Prediction> predictions = new ArrayList<>();
		
		matches.forEach(match -> {
			Prediction prediction = predict(match);
			
			if (prediction != null) {
				predictions.add(prediction);
			}
			else {
				predictions.add(generateEmptyPrediction(match));
			}
		});
		
		return predictions;
	}

	/**
	 * Generates an empty prediction only with the name of local and visitor
	 * 
	 * @param match Match data
	 * @return Empty prediction
	 */
	private Prediction generateEmptyPrediction(Match match) {

		Prediction prediction = new Prediction(match.getLocal(), match.getVisitor());
		prediction.setMessage(Prediction.NO_DATA_AVAILABLE);
		
		return prediction;
	}
	
	/**
	 * Predict result from a match
	 * 
	 * @param match Match
	 * @return Prediction
	 */
	public Prediction predict(Match match) {

		// Get round
		GenericRound round = getRound(match);
		
		if (round == null) {
			round = getRoundFromMatch(match);
		}
		
		return preparePrediction(round);
	}

	/**
	 * Search for a round in the round prediction repository from the data of a match
	 * 
	 * @param match Match data
	 * @return A round, or {@code null} if no round is found
	 */
	private GenericRound getRound(Match match) {
		
		GenericRound round = null;
		
		if (isPrediction) {
			round = Optional.ofNullable(roundPredictionRepository.findByLeagueAndSeasonAndRoundAndLocalAndVisitor(
					match.getLeague(),
					match.getSeason(),
					match.getRound(),
					match.getLocal(),
					match.getVisitor()))
				.orElse(null);
		}
		else {
			round = Optional.ofNullable(roundRepository.findByLeagueAndSeasonAndRoundAndLocalAndVisitor(
					match.getLeague(),
					match.getSeason(),
					match.getRound(),
					match.getLocal(),
					match.getVisitor()))
				.orElse(null);
		}
		
		return round;
	}
	
	/**
	 * Transform match data into round data (always of type {@link RoundPrediction})
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
		
		if (localRound == null || visitorRound == null) {
			throw new QNLException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Round from league '" + match.getLeague() + "', "
					+ "season '" + match.getSeason() + "', "
					+ "round '" + match.getRound() + "' "
					+ "and team '" + (localRound == null ? match.getLocal() : match.getVisitor()) + "' "
					+ "does not exists");
		}
		
		return new RoundPrediction(
				match.getRound(),
				match.getSeason(),
				match.getLeague(),
				match.getLocal(),
				match.getVisitor(),
				0, // Assumes a draw, so difference of points won't
				0, // change when calculating points statistics
				localRound.getLocal().equals(match.getLocal()) ?
						localRound.getLocalPoints() : localRound.getVisitorPoints(),
				visitorRound.getVisitor().equals(match.getVisitor()) ?
						visitorRound.getVisitorPoints() : visitorRound.getLocalPoints());
	}
	
	/**
	 * Prepare the calculation and estimation of the results of a match from the data of a round
	 * 
	 * @param round Round data
	 * @return The prediction
	 */
	private Prediction preparePrediction(GenericRound round) {
		
		Prediction prediction = null;
		
		// Get points, position and sequence from the match
		int points = statisticsUtils.getPointsBeforeMatch(round);
		Integer position = statisticsUtils.getPositionBeforeMatch(round);
		
		int sequenceSize = round.getRoundNumber() <= qnlDefaultMaxIterations ?
				round.getRoundNumber() - 1 : qnlDefaultMaxIterations;
		String localSequence = getSequence(round, round.getLocal(), sequenceSize);
		String visitorSequence = getSequence(round, round.getVisitor(), sequenceSize);
		
		if (position != null) {
			// Get all types of statistics from the points, position and sequence calculated previously
			List<StatsModelBase> localStats = statisticsType.getStatistic(statisticUsed, points, position, localSequence);
			List<StatsModelBase> visitorStats = statisticsType.getStatistic(statisticUsed, points, position, visitorSequence);
			
			// Calculate predictions
			Prediction localPrediction = calculatePartialPrediction(localStats);
			Prediction visitorPrediction = calculatePartialPrediction(visitorStats);
			
			// Estimate result with predictions calculated before
			prediction = new Prediction(round.getLocal(), round.getVisitor());
			prediction = calculateFinalPrediction(prediction, localPrediction, visitorPrediction);
		}
		
		return prediction;
	}

	/**
	 * Get a sequence of {@code sequenceSize} result elements for a team
	 * 
	 * @param round Round
	 * @param team Team
	 * @param sequenceSize Size of the sequence
	 * @return Sequence of results
	 */
	private String getSequence(GenericRound round, String team, int sequenceSize) {
		
		fifoQueue.clear();
		fifoQueue.setSize(sequenceSize);
		
		List<GenericRound> rounds;
		
		if (isPrediction) {
			rounds = roundPredictionRepository.findByLeagueAndSeasonAndTeamFromRoundToRoundSorted(
					round.getLeagueCode(),
					round.getSeasonCode(),
					team,
					round.getRoundNumber() - sequenceSize,
					round.getRoundNumber(),
					new Sort("roundNumber"));
		}
		else {
			rounds = roundRepository.findByLeagueAndSeasonAndTeamFromRoundToRoundSorted(
					round.getLeagueCode(),
					round.getSeasonCode(),
					team,
					round.getRoundNumber() - sequenceSize,
					round.getRoundNumber(),
					new Sort("roundNumber"));
		}
		
		rounds.stream().forEach(r -> fifoQueue.push(statisticsUtils.calculateResult(team, r)));
		
		return fifoQueue.toStringFromHeadToTail();
	}
	
	/**
	 * Calculates a prediction from a list of statistics
	 * 
	 * @param stats List of statistics
	 * @return Calculated prediction
	 */
	private Prediction calculatePartialPrediction(List<StatsModelBase> stats) {

		Prediction prediction = new Prediction(
				null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
		
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
		
		for (StatsModelBase stat : stats) {
			increasePercentage = increasePercentage.add(statisticsType.getStatisticWeigth(stat));
		}
		
		return increasePercentage.equals(BigDecimal.ZERO) ?
				BigDecimal.ONE : BigDecimal.ONE.divide(increasePercentage, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP);
	}
	
	/**
	 * Calculate a prediction from statistic data increasing the percentage used
	 * 
	 * @param stat Statistic data
	 * @param increasePercentage Percentage to increase
	 * @return The prediction
	 */
	private Prediction calculatePredictionWithIncreasePercentage(StatsModelBase stat, BigDecimal increasePercentage) {
		
		BigDecimal total = new BigDecimal(stat.getLocalWinner() + stat.getTied() + stat.getVisitorWinner());
		
		BigDecimal percentage = statisticsType.getStatisticWeigth(stat).multiply(increasePercentage);
		
		return new Prediction(
				null,
				null,
				null,
				new BigDecimal(stat.getLocalWinner()).divide(total, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP).multiply(percentage),
				new BigDecimal(stat.getTied()).divide(total, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP).multiply(percentage),
				new BigDecimal(stat.getVisitorWinner()).divide(total, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP).multiply(percentage),
				null);
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
	
	/**
	 * Calculate final prediction based on local and visitor predictions
	 *
	 * @param prediction Prediction with local and visitor info
	 * @param localPrediction Local prediction
	 * @param visitorPrediction Visitor prediction
	 * @return Final prediction
	 */
	private Prediction calculateFinalPrediction(Prediction prediction, Prediction localPrediction, Prediction visitorPrediction) {

		BigDecimal winProbability = localPrediction.getLocalWinProbability()
				.add(visitorPrediction.getLocalWinProbability())
				.divide(BIG_DECIMAL_TWO, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP);
		BigDecimal drawProbability = localPrediction.getDrawProbability()
				.add(visitorPrediction.getDrawProbability())
				.divide(BIG_DECIMAL_TWO, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP);
		BigDecimal visitorWinProbability = localPrediction.getVisitorWinProbability()
				.add(visitorPrediction.getVisitorWinProbability())
				.divide(BIG_DECIMAL_TWO, BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP);
		
		Result result =
				winProbability.compareTo(drawProbability) >= 0 && winProbability.compareTo(visitorWinProbability) >= 0 ?
						Result.A :
						drawProbability.compareTo(visitorWinProbability) >= 0 ?
								Result.B :
								Result.C;
		
		prediction.setLocalWinProbability(winProbability);
		prediction.setDrawProbability(drawProbability);
		prediction.setVisitorWinProbability(visitorWinProbability);
		prediction.setPrediction(result);
		
		return prediction;
	}

}
