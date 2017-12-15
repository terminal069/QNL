package es.tml.qnl.services.statistics.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatDifferenceOfPoints;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatDifferenceOfPointsRepository;
import es.tml.qnl.services.statistics.DifferenceOfPointsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DifferenceOfPointsServiceImpl implements DifferenceOfPointsService {

	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	private static final String QNL_POINTS_DRAW = "qnl.points.draw";
	private static final String QNL_POINTS_LOSE = "qnl.points.lose";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
//	@Value("${" + QNL_POINTS_DRAW + "}")
//	private Integer draw;
//	
//	@Value("${" + QNL_POINTS_LOSE + "}")
//	private Integer lose;

	@Autowired
	private StatDifferenceOfPointsRepository statDifferenceOfPointsRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Override
	public void calculateDifferenceOfPoints() {
		
		log.info("------------------- START (calculateDifferenceOfPoints) -------------------");
		
//		statDifferenceOfPointsRepository.deleteAll();
		
		roundRepository.findAll(new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
			calculateResults(round);
		});
		
		log.info("-------------------  END (calculateDifferenceOfPoints)  -------------------");
	}

	private void calculateResults(Round round) {
		
		int localPoints = round.getLocalPoints();
		int visitorPoints = round.getVisitorPoints();
		int difference;
		
		if (round.getLocalRes() - round.getVisitorRes() > 0) {
			difference = localPoints - visitorPoints - win;
		}
		else if (round.getLocalRes() == round.getVisitorRes()) {
			difference = localPoints - visitorPoints;
		}
		else {
			difference = localPoints - visitorPoints + win;
		}
		
		StatDifferenceOfPoints stat = Optional.ofNullable(statDifferenceOfPointsRepository.getStatByDifference(difference))
				.orElse(new StatDifferenceOfPoints(difference));
	}

}
