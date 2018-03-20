package es.tml.qnl.services.prediction.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.beans.prediction.StatisticWeight;
import es.tml.qnl.beans.prediction.WeightResponse;
import es.tml.qnl.exceptions.QNLException;
import es.tml.qnl.model.mongo.Combination;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.Weight;
import es.tml.qnl.repositories.mongo.CombinationRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.services.prediction.WeightService;
import es.tml.qnl.services.statistics.util.StatisticsType;
import es.tml.qnl.services.statistics.util.StatisticsType.StatisticType;
import es.tml.qnl.services.statistics.util.StatisticsUtils;
import es.tml.qnl.util.Predictor;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WeightServiceImpl implements WeightService {

	private static final String COLON = ":";
	private static final int BIG_DECIMAL_BASE_SCALE = 10;
	
	private static final String QNL_STATISTICS_MIN_INCREMENT = "qnl.statistics.minIncrement";
	private static final String QNL_STATISTICS_PERCENTAGE_CONTROL = "qnl.statistics.percentageControl";
	private static final String QNL_STATISTICS_MULTIPLE = "qnl.statistics.multiple";
	
	@Value("${" + QNL_STATISTICS_MIN_INCREMENT + "}")
	private BigDecimal minIncrement;
	
	@Value("${" + QNL_STATISTICS_PERCENTAGE_CONTROL + "}")
	private Double percentageControl;
	
	@Value("${" + QNL_STATISTICS_MULTIPLE + "}")
	private StatisticType statisticUsed;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private CombinationRepository combinationRepository;
	
	@Autowired
	private StatisticsType statisticsType;
	
	@Autowired
	private StatisticsUtils statisticsUtils;
	
	@Autowired
	private Predictor predictor;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private StatisticType[] statistics;
	private Map<StatisticType, BigDecimal> weights;
	private List<Round> testRounds = new ArrayList<>();
	private List<Round> controlRounds = new ArrayList<>();
	private Map<String, Integer> results = new HashMap<>();
	private BigDecimal interval;
	private int totalStats;
	
	@Override
	public WeightResponse calculateWeights(BigDecimal increment) {

		int totalStatistics = StatisticType.getStatisticsFromMultiple(statisticUsed).size();

		validateParams(increment, totalStatistics);

		initialize(increment, totalStatistics);
		
		getRounds();
		
		deleteOldData();
		
		recursiveIteration(increment, totalStatistics);
		
		WeightResponse maxHits = getMaxHits();
		
		checkControlRounds(maxHits);
		
		return maxHits;
	}

	/**
	 * Validate params
	 * 
	 * @param increment Increment used to calculate weights
	 * @param totalStatistics Number of statistics
	 */
	private void validateParams(BigDecimal increment, int totalStatistics) {

		if (increment.divideAndRemainder(minIncrement)[1].compareTo(BigDecimal.ZERO) != 0) {
			throw new QNLException(HttpStatus.BAD_REQUEST, "Increment " + increment + " is not a multiple of " + minIncrement);
		}
		
		if (BigDecimal.ONE.divideAndRemainder(increment)[1].compareTo(BigDecimal.ZERO) != 0) {
			throw new QNLException(HttpStatus.BAD_REQUEST, "Increment " + increment + " doesn't allow round weights");
		}
		
		if (BigDecimal.ONE.divideAndRemainder(increment)[0].compareTo(new BigDecimal(totalStatistics)) == -1) {
			throw new QNLException(HttpStatus.BAD_REQUEST, "Increment " + increment + " generates a number of "
					+ "combinations lesser than the total number of statistics (" + totalStatistics + ")");
		}
	}
	
	/**
	 * Initialize the map of weights, the array of statistics, and all the components needed
	 * 
	 * @param increment Increment used to calculate weights
	 * @param totalStatistics Total number of statistics
	 */
	private void initialize(BigDecimal increment, int totalStatistics) {

		statistics = new StatisticType[totalStatistics];
		weights = new HashMap<>();
		int pos = 0;
		
		for (StatisticType stat : StatisticType.getStatisticsFromMultiple(statisticUsed)) {
			weights.put(stat, BigDecimal.ZERO);
			statistics[pos] = stat;
			pos++;
		}
		
		// Sets statistics type to predictor to use the same as this service
		predictor.setStatisticsType(statisticsType);
		predictor.setPrediction(false);
		
		// Initialize time estimator
		timeLeftEstimator.init(calculateTotalCombinations(increment, totalStatistics));
		
		interval = increment;
		totalStats = statistics.length;
	}
	
	/**
	 * Calculates total number of combinations
	 * 
	 * @param increment Increment used to calculate weights
	 * @param totalStatistics Total number of statistics
	 * @return Total number of statistics
	 */
	private int calculateTotalCombinations(BigDecimal increment, int totalStatistics) {

		log.debug("Calculating total number of combinations");
		
		int total = recursiveCalculateTotalCombinations(increment, totalStatistics, 0);
		
		log.info("Total combinations: {}", total);
		
		weights.keySet().forEach(stat -> weights.put(stat, BigDecimal.ZERO));
		
		return total;
	}

	/**
	 * Recursively, calculates total number of combinations
	 * 
	 * @param increment Increment of each combination of weights
	 * @param iteration Number of the iteration
	 * @param total Actual number of total combinations
	 * @return Total number of statistics until this iteration
	 */
	private int recursiveCalculateTotalCombinations(BigDecimal increment, int iteration, int total) {

		if (iteration > 0) {
			
			iteration--;
			
			for (BigDecimal weight = BigDecimal.ZERO; weight.compareTo(BigDecimal.ONE) <= 0; weight = weight.add(increment)) {
				
				weights.put(statistics[iteration], weight);
				
				if (weightsSumatory().compareTo(BigDecimal.ONE) == 0) {
					
					total++;
				}
				
				total = recursiveCalculateTotalCombinations(increment, iteration, total);
			}
		}
		
		return total;
	}

	/**
	 * Get all rounds from repository and split them into two lists: one to calculate statistics,
	 * and the other to control results based on the weigths
	 */
	private void getRounds() {

		roundRepository.findAll().stream()
			.forEach(round -> {
				if (Math.random() > percentageControl) {
					testRounds.add(round);
				}
				else {
					controlRounds.add(round);
				}
			});
		
		log.info("Total test rounds: {}", testRounds.size());
		log.info("Total control rounds: {}", controlRounds.size());
	}
	
	/**
	 * Deletes data from repository by interval and total statistics calculated 
	 */
	private void deleteOldData() {

//		combinationRepository.deleteByIntervalAndTotalStats(interval, totalStats); // TODO: descomentar
		combinationRepository.deleteAll();
	}
	
	/**
	 * Iteratively, generate each combination of weights. But, for calculating statistics, 
	 * only take count on those whose sumatory is equal to {@code BigDecimal.ONE}
	 * 
	 * @param increment Increment of each combination of weights
	 * @param iteration Number of the iteration
	 */
	private void recursiveIteration(BigDecimal increment, int iteration) {
		
		if (iteration > 0) {
			
			iteration--;
			
			for (BigDecimal weight = BigDecimal.ZERO; weight.compareTo(BigDecimal.ONE) <= 0; weight = weight.add(increment)) {
				
				weights.put(statistics[iteration], weight);
				
				if (weightsSumatory().compareTo(BigDecimal.ONE) == 0) {
					timeLeftEstimator.startPartial();
					
					String key = initializeResultMap();
					calculateStatistics(key);
					saveCombination(key);
					
					timeLeftEstimator.finishPartial();
				}
				
				recursiveIteration(increment, iteration);
			}
		}
	}

	/**
	 * Calculates the sumatory of the weights of all statistics
	 * 
	 * @return Sumatory
	 */
	private BigDecimal weightsSumatory() {
		
		BigDecimal sumatory = BigDecimal.ZERO;
		
		for (StatisticType stat : weights.keySet()) {
			sumatory = sumatory.add(weights.get(stat));
		}
		
		return sumatory;
	}

	/**
	 * Initialize result map to zero with weights as key and return this key
	 * 
	 * @return Key
	 */
	private String initializeResultMap() {

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < statistics.length; i++) {
			sb.append(weights.get(statistics[i]) + COLON);
		}
		
		String key = sb.substring(0, sb.length() - 1).toString();
		results.put(key, 0);
		
		log.debug("Calculating statistics for: {} - Estimated time left: {}", key, timeLeftEstimator.getTimeLeft());
		
		return key;
	}
	
	/**
	 * Calculate statistics for the actual weights
	 * 
	 * @param key Key formed by the weights of the statistics
	 */
	private void calculateStatistics(String key) {

		// change weights in StatisticsType
		Arrays.stream(statistics).forEach(stat -> statisticsType.changeStatisticWeight(stat, weights.get(stat)));
		
		// calculate statistics based on Predictor for each round
		testRounds.forEach(round -> {
			
			if (round.getRoundNumber() > 1) {
				Match match = new Match(
						round.getLeagueCode(),
						round.getSeasonCode(),
						round.getRoundNumber(),
						round.getLocal(),
						round.getVisitor());
				
				Prediction prediction = predictor.predict(match);
				
				if (prediction != null && checkPrediction(prediction, round)) {
					
					results.put(key, results.get(key) + 1);
				}
			}
		});
	}
	
	/**
	 * Save the combination into the repository
	 * 
	 * @param key Code
	 */
	private void saveCombination(String key) {

		Combination combination = new Combination();
		combination.setCode(key);
		combination.setInterval(interval);
		combination.setTotalStats(totalStats);
		combination.setHits(results.get(key));
		combination.setHitPercentage(BigDecimal.valueOf(results.get(key))
				.divide(BigDecimal.valueOf(testRounds.size()), BIG_DECIMAL_BASE_SCALE, RoundingMode.HALF_UP));
		
		List<Weight> weights = new ArrayList<>();
		
		for (int i = 0; i < statistics.length; i++) {
			Weight weight = new Weight();
			weight.setStatistic(statistics[i]);
			weight.setWeight(this.weights.get(statistics[i]));
			weights.add(weight);
		}
		
		combination.setWeights(weights);
		
		combinationRepository.save(combination);
	}

	/**
	 * Checks if the prediction has succeed
	 * 
	 * @param prediction Prediction
	 * @param round Real result of the round
	 * @return {@code true} if hits, {@code false} otherwise
	 */
	private boolean checkPrediction(Prediction prediction, Round round) {

		return statisticsUtils.calculateResult(round.getLocal(), round)
				.equals(prediction.getPrediction());
	}
	
	/**
	 * Search into results to find out what combination of weights has the higher rate of hits
	 * 
	 * @return The combination of weights with the higher rate of hits
	 */
	private WeightResponse getMaxHits() {

		log.info("Looking for the combination with the higher rate of hits from {} combinations", results.size());
		
		List<StatisticWeight> statisticWeights = new ArrayList<>();
		
		String[] weightsArray = results.entrySet().stream()
				.max(Entry.comparingByValue())
				.orElseThrow(() -> new QNLException(HttpStatus.NOT_FOUND, "No combination of weights found"))
			.getKey()
			.split(COLON);
		
		for (int i = 0; i < statistics.length; i++) {
			StatisticWeight statisticWeight = new StatisticWeight(
					statistics[i].getName(),
					new BigDecimal(weightsArray[i]));
			
			statisticWeights.add(statisticWeight);
		}
		
		return new WeightResponse(statisticWeights);
	}
	
	/**
	 * Checks control rounds to see if they meet with the same weights calculated with test rounds
	 *   
	 * @param maxHits Weights with max hits
	 */
	private void checkControlRounds(WeightResponse maxHits) {

		log.info("Checking control rounds");
		
		// change weights in StatisticsType
		maxHits.getStatisticWeights().forEach(statisticWeight -> statisticsType.changeStatisticWeight(
				StatisticType.getStatisticTypeFromName(statisticWeight.getName()),
				statisticWeight.getWeight()));
		
		StringBuilder sb = new StringBuilder();
		
		for (StatisticWeight statisticWeight : maxHits.getStatisticWeights()) {
			sb.append(statisticWeight.getWeight() + COLON);
		}

		String key = sb.substring(0, sb.length() - 1).toString();
		final String controlKey = "CONTROL:" + key;
		results.put(controlKey, 0);
		
		// calculate statistics based on Predictor for each round
		controlRounds.forEach(round -> {
			
			if (round.getRoundNumber() > 1) {
				Match match = new Match(
						round.getLeagueCode(),
						round.getSeasonCode(),
						round.getRoundNumber(),
						round.getLocal(),
						round.getVisitor());
				
				Prediction prediction = predictor.predict(match);
				
				if (prediction != null && checkPrediction(prediction, round)) {
					
					results.put(controlKey, results.get(controlKey) + 1);
				}
			}
		});
		
		maxHits.setTestHitPercentage(BigDecimal.valueOf(results.get(key))
				.divide(
					BigDecimal.valueOf(testRounds.size()),
					BIG_DECIMAL_BASE_SCALE,
					RoundingMode.HALF_UP));
		
		maxHits.setControlHitPercentage(BigDecimal.valueOf(results.get(controlKey))
				.divide(
					BigDecimal.valueOf(controlRounds.size()),
					BIG_DECIMAL_BASE_SCALE,
					RoundingMode.HALF_UP));
		
		log.info("Difference of hit percentage between test ({}) and control ({}) rounds: {}",
				testRounds.size(),
				controlRounds.size(),
				maxHits.getTestHitPercentage().subtract(maxHits.getControlHitPercentage()));
		
		results.remove(controlKey);
	}

}
