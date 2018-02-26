package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPointsPositionSequence;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.StatPointsPositionSequenceRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PointsPositionSequenceStatType extends BaseStatType {

	@Autowired
	private StatPointsPositionSequenceRepository statPointsPositionSequenceRepository;
	
	@Override
	public void deleteOldData() {

		statPointsPositionSequenceRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result, boolean isLocal) {

		if (isLocal) {
			StatPointsPositionSequence stat = Optional
					.ofNullable(statPointsPositionSequenceRepository.findByPointsAndPositionAndSequence(points, position, sequence))
					.orElse(new StatPointsPositionSequence(points, position, sequence));
			setResult(stat, result);
			statPointsPositionSequenceRepository.save(stat);
		}
	}

	@Override
	public StatsModelBase getStatistic(Integer points, Integer position, String sequence) {

		return statPointsPositionSequenceRepository.findByPointsAndPositionAndSequence(points, position, sequence);
	}

}
