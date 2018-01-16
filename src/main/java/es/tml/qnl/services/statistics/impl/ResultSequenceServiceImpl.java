package es.tml.qnl.services.statistics.impl;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.ResultSequenceRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatResultSequence;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatResultSequenceRepository;
import es.tml.qnl.services.statistics.ResultSequenceService;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
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
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private int maxIterations;
	private int totalTeams;
	private int posActualTeam;
	
	@Override
	public void calculateResultSequence(ResultSequenceRequest request) {

		maxIterations = request.getMaxIterations();
		totalTeams = Teams.getTeams().size();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		// Delete old data
		statResultSequenceRepository.deleteAll();
		
		// Iterate and calculate results for each sequence
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.clear();
			fifoQueue.setSize(iterationNumber);
			performIteration(iterationNumber);
		});
	}
	
	private void performIteration(int iterationNumber) {
		
		log.debug("Performing iteration with a sequence of {} elements", iterationNumber);
		
		posActualTeam = 1;
		
		Teams.getTeams().forEach(team -> {
			log.debug("Iteration {}/{} - Team {}/{} - Estimated time left: {}",
					iterationNumber, maxIterations, posActualTeam, totalTeams, timeLeftEstimator.getTimeLeft());
			
			timeLeftEstimator.startPartial();
			roundRepository.findByTeamSorted(team, new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
				calculateSequence(
						round,
						iterationNumber,
						calculateResult(team, round));
			});
			
			posActualTeam++;
			timeLeftEstimator.finishPartial();
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
	
	private void calculateSequence(Round round, int iterationNumber, Result result) {
		
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
