package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatSequence;
import es.tml.qnl.model.mongo.StatsModelBase;
import es.tml.qnl.repositories.mongo.StatSequenceRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class SequenceStatType extends BaseStatType {

	@Autowired
	private StatSequenceRepository statSequenceRepository;
	
	@Override
	public void deleteOldData() {

		statSequenceRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result, boolean isLocal) {

		StatSequence stat = Optional
				.ofNullable(statSequenceRepository.findBySequence(sequence))
				.orElse(new StatSequence(sequence));
		setResult(stat, result);
		statSequenceRepository.save(stat);
	}

	@Override
	public StatsModelBase getStatistic(Integer points, Integer position, String sequence) {

		return statSequenceRepository.findBySequence(sequence);
	}

}
