package es.tml.qnl.services.statistics.impl;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.StatisticsRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.enums.StatisticsType;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatPointsPositionRepository;
import es.tml.qnl.repositories.mongo.StatPointsPositionSequenceRepository;
import es.tml.qnl.repositories.mongo.StatPointsRepository;
import es.tml.qnl.repositories.mongo.StatPointsSequenceRepository;
import es.tml.qnl.repositories.mongo.StatPositionRepository;
import es.tml.qnl.repositories.mongo.StatPositionSequenceRepository;
import es.tml.qnl.repositories.mongo.StatSequenceRepository;
import es.tml.qnl.services.statistics.StatisticsService;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
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
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	@Autowired
	private StatPointsRepository statPointsRepository;
	
	@Autowired
	private StatPositionRepository statPositionRepository;
	
	@Autowired
	private StatSequenceRepository statSequenceRepository;
	
	@Autowired
	private StatPointsPositionRepository statPointsPositionRepository;
	
	@Autowired
	private StatPointsSequenceRepository statPointsSequenceRepository;
	
	@Autowired
	private StatPositionSequenceRepository statPositionSequenceRepository;
	
	@Autowired
	private StatPointsPositionSequenceRepository statPointsPositionSequenceRepository;
	
	private int minRound;
	private int maxIterations;
	private int totalTeams;
	private int posActualTeam;
	
	@Override
	public void calculateStatistics(StatisticsRequest request, StatisticsType statisticsType) {

		initializeRequestParameters(request);
		
		totalTeams = Teams.getTeams().size();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		deleteOldData(statisticsType);
		
		// Calculate statistics for each team
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.setSize(iterationNumber);
//			performIteration(iterationNumber);
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
	
	private void deleteOldData(StatisticsType statisticsType) {
		
		log.debug("Deleting old data for '{}' statistics type", statisticsType.name());
		
		switch(statisticsType) {
			case POINTS: {
				statPointsRepository.deleteAll();
				break;
			}
			case POSITION: {
				statPositionRepository.deleteAll();
				break;
			}
			case SEQUENCE: {
				statSequenceRepository.deleteAll();
				break;
			}
			case POINTS_POSITION: {
				statPointsPositionRepository.deleteAll();
				break;
			}
			case POINTS_SEQUENCE: {
				statPointsSequenceRepository.deleteAll();
				break;
			}
			case POSITION_SEQUENCE: {
				statPositionSequenceRepository.deleteAll();
				break;
			}
			case POINTS_POSITION_SEQUENCE: {
				statPointsPositionSequenceRepository.deleteAll();
				break;
			}
		}
	}

}
