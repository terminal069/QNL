package es.tml.qnl.services.statistics.impl;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.ClassPosResSeqRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatClassPosResSeq;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatClassPosResSeqRepository;
import es.tml.qnl.services.statistics.ClassPosWithResSeqService;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassPosWithResSeqServiceImpl implements ClassPosWithResSeqService {

	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	@Autowired
	private StatClassPosResSeqRepository statClassPosResSeqRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private int minRound;
	
	private int maxIterations;
	
	private int totalTeams = Teams.getTeams().size();
	
	private int posActualTeam;
	
	@Override
	public void calculateClassPosWithResSeq(ClassPosResSeqRequest request) {

		this.minRound = request.getMinRound();
		this.maxIterations = request.getMaxIterations();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		// Delete old data
		statClassPosResSeqRepository.deleteAll();
		
		// Calculate statistics for each team
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.setSize(iterationNumber);
			performIteration(iterationNumber);
		});
	}

	private void performIteration(int iterationNumber) {

		log.debug("Performing iteration with a sequence of {} elements", iterationNumber);
		
		posActualTeam = 1;
		
		Teams.getTeams().forEach(team -> {
			
			timeLeftEstimator.startPartial();
			
			log.debug("Iteration {}/{} - Team {}/{} - Estimated time left: {}",
					iterationNumber, maxIterations, posActualTeam, totalTeams, timeLeftEstimator.getTimeLeft());
			
			fifoQueue.clear();
			roundRepository.findByTeamSorted(team, new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
				calculatePositionAndSequence(
						round,
						iterationNumber,
						calculateResult(team, round),
						team);
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
	
	private void calculatePositionAndSequence(Round round, int iterationNumber, Result result, String team) {

		Integer previousPositionDifference = null;
		
		if (fifoQueue.getQueueSize() == iterationNumber
				&& round.getRoundNumber() > iterationNumber
				&& round.getRoundNumber() >= minRound
				&& (previousPositionDifference = calculatePreviousPosition(
						round.getLeagueCode(),
						round.getSeasonCode(),
						round.getRoundNumber(),
						team)) != null) {
			
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			StatClassPosResSeq statClassPosResSeq = Optional
				.ofNullable(statClassPosResSeqRepository.findByPositionDifferenceAndSequence(previousPositionDifference, sequence))
				.orElse(new StatClassPosResSeq(previousPositionDifference, sequence));
			
			switch(result) {
				case A: {
					statClassPosResSeq.setLocalWinner(statClassPosResSeq.getLocalWinner() + 1);
					break;
				}
				case B: {
					statClassPosResSeq.setTied(statClassPosResSeq.getTied() + 1);
					break;
				}
				case C: {
					statClassPosResSeq.setVisitorWinner(statClassPosResSeq.getVisitorWinner() + 1);
					break;
				}
			}
			
			statClassPosResSeqRepository.save(statClassPosResSeq);
		}
		
		fifoQueue.push(result);
	}

	private Integer calculatePreviousPosition(String leagueCode, int seasonCode, int roundNumber, String team) {
		
		Integer previousPositionDifference = null;
		
		if (roundNumber > 1) {
			
			Round round = roundRepository.findbyLeagueAndSeasonAndRoundAndTeam(
					leagueCode,
					seasonCode,
					roundNumber - 1,
					team);
			
			if (round != null) {
				previousPositionDifference = round.getLocalPosition() - round.getVisitorPosition();
			}
		}
		
		return previousPositionDifference;
	}

}
