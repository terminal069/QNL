package es.tml.qnl.services.statistics.impl;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatResultSequence;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatResultSequenceRepository;
import es.tml.qnl.services.statistics.ResultSequenceService;
import es.tml.qnl.util.FIFOQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResultSequenceServiceImpl implements ResultSequenceService {

	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	@Autowired
	private StatResultSequenceRepository statResultSequenceRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Override
	public void calculateResultSequence(int maxIterations) {

		log.info("------------------- START (calculateResultSequence) -------------------");
		
		statResultSequenceRepository.deleteAll();
		
		// Iterate and calculate results for each sequence
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.clear();
			fifoQueue.setSize(iterationNumber);
			performIteration(iterationNumber);
		});
		
		log.info("-------------------  END (calculateResultSequence)  -------------------");
	}
	
	private void performIteration(int iterationNumber) {
		
		log.info("Performing iteration with a sequence of {} elements", iterationNumber);
		
		Teams.getTeams().forEach(team -> {
			roundRepository.getRoundByTeam(team, new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
				Result result = calculateResult(team, round);
				calculateSequence(round, result, iterationNumber);
			});
		});
		
	}
	
	private Result calculateResult(String name, Round round) {
		
		Result result = null;
		
		if (round.getLocal().equals(name)) {
			result = round.getLocalRes() > round.getVisitorRes() ? Result.A :
				round.getLocalRes() == round.getVisitorRes() ? Result.B : Result.C;
		}
		else {
			result = round.getVisitorRes() > round.getLocalRes() ? Result.A :
				round.getVisitorRes() == round.getLocalRes() ? Result.B : Result.C;
		}
		
		return result;
	}
	
	private void calculateSequence(Round round, Result result, int iterationNumber) {
		
		if (fifoQueue.getQueueSize() == iterationNumber
				&& round.getRoundNumber() > iterationNumber) {
			
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			StatResultSequence statResultSequence = Optional.ofNullable(statResultSequenceRepository.findBySequence(sequence))
				.orElse(new StatResultSequence(sequence));
			
			switch(result) {
				case A: {
					statResultSequence.setLocalWinner(statResultSequence.getLocalWinner() + 1);
					break;
				}
				case B: {
					statResultSequence.setTied(statResultSequence.getTied() + 1);
					break;
				}
				case C: {
					statResultSequence.setVisitorWinner(statResultSequence.getVisitorWinner() + 1);
					break;
				}
			}
			
			statResultSequenceRepository.save(statResultSequence);
		}
		
		fifoQueue.push(result);
	}

}
