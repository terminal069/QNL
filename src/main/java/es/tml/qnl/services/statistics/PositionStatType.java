package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPosition;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.StatPositionRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PositionStatType extends BaseStatType {

	@Autowired
	private StatPositionRepository statPositionRepository;
	
	@Override
	public void deleteOldData() {

		statPositionRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result) {

		StatPosition stat = Optional
				.ofNullable(statPositionRepository.findByPosition(position))
				.orElse(new StatPosition(position));
		setResult(stat, result);
		statPositionRepository.save(stat);
	}

	@Override
	public StatsModelBase getStatistic(Integer points, Integer position, String sequence) {
		
		return statPositionRepository.findByPosition(position);
	}
}
