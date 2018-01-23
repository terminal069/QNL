package es.tml.qnl.services.statistics.impl;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.ResultSequenceRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatPointsSequence;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatPointsSequenceRepository;
import es.tml.qnl.services.statistics.DiffPointsWithResSeqService;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiffPointsWithResSeqServiceImpl implements DiffPointsWithResSeqService {

	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private StatPointsSequenceRepository statDiffPointsResSeqRepository;
	
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
	public void calculateDiffPointsWithResSeq(ResultSequenceRequest request) {

		maxIterations = request.getMaxIterations();
		totalTeams = Teams.getTeams().size();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		// Delete all data from repository
		statDiffPointsResSeqRepository.deleteAll();
		
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
				calculateDifferenceAndSequence(
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
	
	private void calculateDifferenceAndSequence(Round round, int iterationNumber, Result result) {
		
		if (fifoQueue.getQueueSize() == iterationNumber
				&& round.getRoundNumber() > iterationNumber) {
			
			int difference = getDifferenceBeforeMatch(round);
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			StatPointsSequence statDiffPointsResSeq = Optional
					.ofNullable(statDiffPointsResSeqRepository.findByPointsAndSequence(difference, sequence))
					.orElse(new StatPointsSequence(difference, sequence));
			
			switch(result) {
				case A: {
					statDiffPointsResSeq.setLocalWinner(statDiffPointsResSeq.getLocalWinner() + 1);
					break;
				}
				case B: {
					statDiffPointsResSeq.setTied(statDiffPointsResSeq.getTied() + 1);
					break;
				}
				case C: {
					statDiffPointsResSeq.setVisitorWinner(statDiffPointsResSeq.getVisitorWinner() + 1);
					break;
				}
			}
			
			statDiffPointsResSeqRepository.save(statDiffPointsResSeq);
		}
		
		fifoQueue.push(result);
	}

	private int getDifferenceBeforeMatch(Round round) {
		
		int difference;
		int localPoints = round.getLocalPoints();
		int visitorPoints = round.getVisitorPoints();
		
		if (round.getLocalRes() - round.getVisitorRes() > 0) {
			difference = localPoints - visitorPoints - win;
		}
		else if (round.getLocalRes() == round.getVisitorRes()) {
			difference = localPoints - visitorPoints;
		}
		else {
			difference = localPoints - visitorPoints + win;
		}
		
		return difference;
	}

}
