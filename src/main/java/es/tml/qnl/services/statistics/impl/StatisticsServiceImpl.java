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
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
import es.tml.qnl.util.enums.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

	private static final int DEFAULT_MIN_ROUND = 1;
	private static final int DEFAUL_MAX_ITERATIONS = 1;
	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private StatisticsType statisticsType;
	
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

	private void initializeRequestParameters(StatisticsRequest request) {

		if (request == null) {
			
			minRound = DEFAULT_MIN_ROUND;
			maxIterations = DEFAUL_MAX_ITERATIONS;
		}
		else {
			minRound = request.getMinRound() == null ? DEFAULT_MIN_ROUND : request.getMinRound();
			maxIterations = request.getMaxIterations() == null ? DEFAUL_MAX_ITERATIONS : request.getMaxIterations();
		}
	}
	
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
						calculateResult(team, round),
						team);
			});
			
			posActualTeam++;
			timeLeftEstimator.finishPartial();
		});
	}
	
	private Result calculateResult(String name, Round round) {
		
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
	
	private void processStatistic(Round round, int iterationNumber, Result result, String team) {

		Integer position = null;
		
		if (fifoQueue.getQueueSize() == iterationNumber
				&& round.getRoundNumber() > iterationNumber
				&& round.getRoundNumber() >= minRound
				&& (position = calculatePreviousPosition(
						round.getLeagueCode(),
						round.getSeasonCode(),
						round.getRoundNumber(),
						team)) != null) {
			
			Integer points = getDifferenceBeforeMatch(round);
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			statisticsType.saveStatistic(statisticType, points, position, sequence, result);
		}
		
		fifoQueue.push(result);
	}
	
	private Integer calculatePreviousPosition(String leagueCode, int seasonCode, int roundNumber, String team) {
		
		Integer previousPositionDifference = null;
		
		if (roundNumber > 1) {
			
			Round round = roundRepository.findbyLeagueAndSeasonAndRoundAndTeam(
					leagueCode,
					seasonCode,
					roundNumber - 1,
					team);
			
			if (round != null) {
				previousPositionDifference = round.getLocalPosition() - round.getVisitorPosition();
			}
		}
		
		return previousPositionDifference;
	}
	
	private int getDifferenceBeforeMatch(Round round) {
		
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

}
