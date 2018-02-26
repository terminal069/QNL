package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPointsSequence;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.StatPointsSequenceRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PointsSequenceStatType extends BaseStatType {

	@Autowired
	private StatPointsSequenceRepository statPointsSequenceRepository;
	
	@Override
	public void deleteOldData() {

		statPointsSequenceRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result, boolean isLocal) {

		if (isLocal) {
			StatPointsSequence stat = Optional
					.ofNullable(statPointsSequenceRepository.findByPointsAndSequence(points, sequence))
					.orElse(new StatPointsSequence(points, sequence));
			setResult(stat, result);
			statPointsSequenceRepository.save(stat);
		}
	}

	@Override
	public StatsModelBase getStatistic(Integer points, Integer position, String sequence) {
		return statPointsSequenceRepository.findByPointsAndSequence(points, sequence);
	}

}
