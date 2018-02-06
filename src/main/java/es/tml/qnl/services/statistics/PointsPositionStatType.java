package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPointsPosition;
import es.tml.qnl.repositories.mongo.StatPointsPositionRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PointsPositionStatType extends BaseStatType {
	
	@Autowired
	private StatPointsPositionRepository statPointsPositionRepository;
	
	@Override
	public void deleteOldData() {

		statPointsPositionRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result) {

		StatPointsPosition stat = Optional
				.ofNullable(statPointsPositionRepository.findByPointsAndPosition(points, position))
				.orElse(new StatPointsPosition(points, position));
		setResult(stat, result);
		statPointsPositionRepository.save(stat);
	}

}
