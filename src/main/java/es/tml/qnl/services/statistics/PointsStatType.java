package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPoints;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.StatPointsRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PointsStatType extends BaseStatType {
	
	@Autowired
	private StatPointsRepository statPointsRepository;

	@Override
	public void deleteOldData() {

		statPointsRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result, boolean isLocal) {

		if (isLocal) {
			StatPoints stat = Optional
					.ofNullable(statPointsRepository.findByPoints(points))
					.orElse(new StatPoints(points));
			setResult(stat, result);
			statPointsRepository.save(stat);
		}
	}

	@Override
	public StatsModelBase getStatistic(Integer points, Integer position, String sequence) {
		return statPointsRepository.findByPoints(points);
	}
}
