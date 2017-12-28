package es.tml.qnl.services.statistics.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.data.Teams;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatAVsB;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatAVsBRepository;
import es.tml.qnl.services.statistics.AVsBService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AVsBServiceImpl implements AVsBService {

	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private StatAVsBRepository statAVsBRepository;
	
	@Override
	public void calculateAVsB() {
		
		// Delete data from statAVsB repository
		statAVsBRepository.deleteAll();
		
		// Perform combinatory of all teams
		Teams.getTeams().forEach(local -> {
			Teams.getTeams().forEach(visitor -> {
				searchRounds(local, visitor);
			});
		});
	}

	private void searchRounds(String local, String visitor) {
		
		log.debug("Performing combination of {} - {}", local, visitor);
		
		if (!local.equals(visitor)) {
			roundRepository.getRoundByLocalVisitor(local, visitor)
				.forEach(round -> {
					calculateAndPersistResults(round, local, visitor);
				});
		}
	}

	private void calculateAndPersistResults(Round round, String local, String visitor) {
		
		StatAVsB statAVsB = Optional.ofNullable(statAVsBRepository.getStatAVsBByLocalVisitor(local, visitor))
				.orElse(new StatAVsB(local, visitor));
		
		if (round.getLocalRes() > round.getVisitorRes()) {
			statAVsB.setLocalWinner(statAVsB.getLocalWinner() + 1);
		}
		else if (round.getLocalRes() == round.getVisitorRes()) {
			statAVsB.setTied(statAVsB.getTied() + 1);
		}
		else {
			statAVsB.setVisitorWinner(statAVsB.getVisitorWinner() + 1);
		}
		
		statAVsBRepository.save(statAVsB);
	}

}
