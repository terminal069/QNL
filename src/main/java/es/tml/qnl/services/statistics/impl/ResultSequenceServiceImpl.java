package es.tml.qnl.services.statistics.impl;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatResultSequenceRepository;
import es.tml.qnl.repositories.mongo.TeamRepository;
import es.tml.qnl.services.statistics.ResultSequenceService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResultSequenceServiceImpl implements ResultSequenceService {

	@Autowired
	StatResultSequenceRepository statResultSequenceRepository;
	
	@Autowired
	RoundRepository roundRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Override
	public void calculateResultSequence(int maxIterations) {

		// Delete data from statResultSequence repository
//		statResultSequenceRepository.deleteAll();
		
		// Iterate and calculate results for each sequence
		IntStream.rangeClosed(1, maxIterations).forEach(i -> {
			iteration(i);
		});
	}
	
	private void iteration(int i) {
		
		log.info("Performing iteration with a sequence of {} elements", i);
		
		teamRepository.findAll().forEach(team -> {
			roundRepository.getRoundByTeam(team.getName(), new Sort("seasonCode", "roundNumber")).forEach(round -> {
				log.info("{}: {} - {}", team.getName(), round.getSeasonCode(), round.getRoundNumber());
			});
		});
		
	}

}
