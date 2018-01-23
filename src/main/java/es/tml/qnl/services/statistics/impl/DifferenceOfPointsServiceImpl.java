package es.tml.qnl.services.statistics.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatPoints;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatPointsRepository;
import es.tml.qnl.services.statistics.DifferenceOfPointsService;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DifferenceOfPointsServiceImpl implements DifferenceOfPointsService {

	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private StatPointsRepository statDifferenceOfPointsRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private int actualRound;
	
	@Override
	public void calculateDifferenceOfPoints() {
		
		List<Round> rounds = roundRepository.findAll();
		actualRound = 1;
		int totalRounds = rounds.size();
		timeLeftEstimator.init(totalRounds);
		
		// Delete difference of points statistics data from repository
		statDifferenceOfPointsRepository.deleteAll();
		
		// Search all rounds and calculate statistics for each one
		rounds.forEach(round -> {
			log.debug("Performing iteration of round {}/{} - Estimated time left: {}",
					actualRound, totalRounds, timeLeftEstimator.getTimeLeft());
			
			timeLeftEstimator.startPartial();
			calculateResults(round);
			actualRound++;
			timeLeftEstimator.finishPartial();
		});
	}

	private void calculateResults(Round round) {
		
		int localPoints = round.getLocalPoints();
		int visitorPoints = round.getVisitorPoints();
		int difference;
		Result result = null;
		
		// Get difference before match and assign result
		if (round.getLocalRes() - round.getVisitorRes() > 0) {
			difference = localPoints - visitorPoints - win;
			result = Result.A;
		}
		else if (round.getLocalRes() == round.getVisitorRes()) {
			difference = localPoints - visitorPoints;
			result = Result.B;
		}
		else {
			difference = localPoints - visitorPoints + win;
			result = Result.C;
		}
		
		// Get difference of points statistics and, if it doesn't exists, create one
		StatPoints statDifferenceOfPoints = Optional.ofNullable(statDifferenceOfPointsRepository.findByPoints(difference))
				.orElse(new StatPoints(difference));
		
		// Depending the result, increase the value of statistics
		switch(result) {
			case A: {
				statDifferenceOfPoints.setLocalWinner(statDifferenceOfPoints.getLocalWinner() + 1);
				break;
			}
			case B: {
				statDifferenceOfPoints.setTied(statDifferenceOfPoints.getTied() + 1);
				break;
			}
			case C: {
				statDifferenceOfPoints.setVisitorWinner(statDifferenceOfPoints.getVisitorWinner() + 1);
				break;
			}
		}
		
		// Save statistics
		statDifferenceOfPointsRepository.save(statDifferenceOfPoints);
	}

}
