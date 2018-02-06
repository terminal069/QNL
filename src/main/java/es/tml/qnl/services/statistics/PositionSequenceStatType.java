package es.tml.qnl.services.statistics;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.model.mongo.StatPositionSequence;
import es.tml.qnl.repositories.mongo.StatPositionSequenceRepository;
import es.tml.qnl.util.enums.Result;

@Service
public class PositionSequenceStatType extends BaseStatType {

	@Autowired
	private StatPositionSequenceRepository statPositionSequenceRepository;
	
	@Override
	public void deleteOldData() {

		statPositionSequenceRepository.deleteAll();
	}

	@Override
	public void saveStatistic(Integer points, Integer position, String sequence, Result result) {

		StatPositionSequence stat = Optional
				.ofNullable(statPositionSequenceRepository.findByPositionAndSequence(position, sequence))
				.orElse(new StatPositionSequence(position, sequence));
		setResult(stat, result);
		statPositionSequenceRepository.save(stat);
	}

}
