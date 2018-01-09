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
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AVsBServiceImpl implements AVsBService {

	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private StatAVsBRepository statAVsBRepository;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private int totalTeams = Teams.getTeams().size();
	
	private int totalCombitations = totalTeams * totalTeams;
	
	private int teamPosition = 1;
	
	@Override
	public void calculateAVsB() {
		
		timeLeftEstimator.init(totalCombitations);
		
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
		
		log.debug("Performing combination {}/{} - Estimated time left: {}",
				teamPosition, totalCombitations, timeLeftEstimator.getTimeLeft());
		
		timeLeftEstimator.startPartial();
		
		if (!local.equals(visitor)) {
			roundRepository.findByLocalAndVisitor(local, visitor)
				.forEach(round -> {
					calculateAndPersistResults(round, local, visitor);
				});
		}
		
		teamPosition++;
		timeLeftEstimator.finishPartial();
	}

	private void calculateAndPersistResults(Round round, String local, String visitor) {
		
		StatAVsB statAVsB = Optional.ofNullable(statAVsBRepository.findByLocalAndVisitor(local, visitor))
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
