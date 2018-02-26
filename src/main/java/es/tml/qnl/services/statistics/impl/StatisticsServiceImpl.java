package es.tml.qnl.services.statistics.impl;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.StatisticsRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.services.statistics.StatisticsService;
import es.tml.qnl.services.statistics.util.StatisticsType;
import es.tml.qnl.services.statistics.util.StatisticsType.StatisticType;
import es.tml.qnl.services.statistics.util.StatisticsUtils;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
import es.tml.qnl.util.enums.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

	private static final int DEFAULT_MIN_ROUND = 1;
	private static final int DEFAULT_MAX_ITERATIONS = 1;
	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	private static final String QNL_DEFAULT_MAX_ITERATIONS = "qnl.defaultMaxIterations";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Value("${" + QNL_DEFAULT_MAX_ITERATIONS + "}")
	private Integer qnlDefaultMaxIterations;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private StatisticsType statisticsType;
	
	@Autowired
	private StatisticsUtils statisticsUtils;
	
	private int minRound;
	private int maxIterations;
	private int totalTeams;
	private int posActualTeam;
	private StatisticType statisticType;
	
	@Override
	public void calculateStatistics(StatisticsRequest request, StatisticType statisticType) {

		initializeRequestParameters(request);
		
		totalTeams = Teams.getTeams().size();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		this.statisticType = statisticType;
		statisticsType.deleteOldData(this.statisticType);
		
		// Calculate statistics for each team
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.setSize(iterationNumber);
			performIteration(iterationNumber);
		});
	}

	/**
	 * Initialize request paremeters
	 * 
	 * @param request Request data
	 */
	private void initializeRequestParameters(StatisticsRequest request) {

		if (request == null) {
			minRound = DEFAULT_MIN_ROUND;
			maxIterations = DEFAULT_MAX_ITERATIONS;
		}
		else {
			minRound = request.getMinRound() == null ? DEFAULT_MIN_ROUND : request.getMinRound();
			maxIterations = request.getMaxIterations() == null ? DEFAULT_MAX_ITERATIONS : request.getMaxIterations();
		}
		
		maxIterations = maxIterations > qnlDefaultMaxIterations ? qnlDefaultMaxIterations : maxIterations;
	}
	
	/**
	 * Iterate for each team
	 * 
	 * @param iterationNumber Iteration number
	 */
	private void performIteration(int iterationNumber) {

		posActualTeam = 1;
		
		Teams.getTeams().forEach(team -> {
			
			log.debug("Iteration {}/{} - Team {}/{} - Estimated time left: {}",
					iterationNumber, maxIterations, posActualTeam, totalTeams, timeLeftEstimator.getTimeLeft());
			
			timeLeftEstimator.startPartial();
			fifoQueue.clear();
			roundRepository.findByTeamSorted(team, new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
				processStatistic(
						round,
						iterationNumber,
						statisticsUtils.calculateResult(team, round),
						team);
			});
			
			posActualTeam++;
			timeLeftEstimator.finishPartial();
		});
	}
	
	/**
	 * Save statistic based on statistic type
	 * 
	 * @param round Round
	 * @param iterationNumber Iteration number
	 * @param result Result
	 * @param team Team
	 */
	private void processStatistic(Round round, int iterationNumber, Result result, String team) {

		Integer position = null;
		
		if (fifoQueue.getQueueSize() == iterationNumber
				&& round.getRoundNumber() > iterationNumber
				&& round.getRoundNumber() >= minRound
				&& (position = statisticsUtils.getPositionBeforeMatch(round)) != null) {
			
			Integer points = statisticsUtils.getPointsBeforeMatch(round);
			String sequence = fifoQueue.toStringFromHeadToTail();
			boolean isLocal = team.equals(round.getLocal());
			
			statisticsType.saveStatistic(statisticType, points, position, sequence, result, isLocal);
		}
		
		fifoQueue.push(result);
	}
}
