package es.tml.qnl.services.statistics.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatDifferenceOfPoints;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatDifferenceOfPointsRepository;
import es.tml.qnl.services.statistics.DifferenceOfPointsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DifferenceOfPointsServiceImpl implements DifferenceOfPointsService {

	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private StatDifferenceOfPointsRepository statDifferenceOfPointsRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Override
	public void calculateDifferenceOfPoints() {
		
		log.info("------------------- START (calculateDifferenceOfPoints) -------------------");
		
		// Delete difference of points statistics data from repository
		statDifferenceOfPointsRepository.deleteAll();
		
		// Search all rounds and calculate statistics for each one
		roundRepository.findAll().forEach(round -> {
			calculateResults(round);
		});
		
		log.info("-------------------  END (calculateDifferenceOfPoints)  -------------------");
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
		StatDifferenceOfPoints statDifferenceOfPoints = Optional.ofNullable(statDifferenceOfPointsRepository.getStatByDifference(difference))
				.orElse(new StatDifferenceOfPoints(difference));
		
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
