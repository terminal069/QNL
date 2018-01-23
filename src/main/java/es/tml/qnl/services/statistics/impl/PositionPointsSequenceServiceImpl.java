package es.tml.qnl.services.statistics.impl;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.ClassPosResSeqRequest;
import es.tml.qnl.data.Teams;
import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatPointsPositionSequence;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatPointsPositionSequenceRepository;
import es.tml.qnl.services.statistics.PositionPointsSequenceService;
import es.tml.qnl.util.FIFOQueue;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PositionPointsSequenceServiceImpl implements PositionPointsSequenceService {

	private static final String SEASON_CODE = "seasonCode";
	private static final String ROUND_NUMBER = "roundNumber";
	
	private static final String QNL_POINTS_WIN = "qnl.points.win";
	
	@Value("${" + QNL_POINTS_WIN + "}")
	private Integer win;
	
	@Autowired
	private StatPointsPositionSequenceRepository statPositionPointsSequenceRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private int minRound;
	private int maxIterations;
	private int totalTeams;
	private int posActualTeam;
	
	@Override
	public void calculatePosDiffSeq(ClassPosResSeqRequest request) {

		this.minRound = request.getMinRound();
		this.maxIterations = request.getMaxIterations();
		totalTeams = Teams.getTeams().size();
		timeLeftEstimator.init(maxIterations * totalTeams);
		
		// Delete old data
		statPositionPointsSequenceRepository.deleteAll();
		
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
			
			log.debug("Iteration {}/{} - Team {}/{} - Estimated time left: {}",
					iterationNumber, maxIterations, posActualTeam, totalTeams, timeLeftEstimator.getTimeLeft());
			
			timeLeftEstimator.startPartial();
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
			
			int difference = getDifferenceBeforeMatch(round);
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			StatPointsPositionSequence statPositionPointsSequence = Optional
				.ofNullable(statPositionPointsSequenceRepository
						.findByPointsAndPositionAndSequence(difference, previousPositionDifference, sequence))
				.orElse(new StatPointsPositionSequence(previousPositionDifference, difference, sequence));
			
			switch(result) {
				case A: {
					statPositionPointsSequence.setLocalWinner(statPositionPointsSequence.getLocalWinner() + 1);
					break;
				}
				case B: {
					statPositionPointsSequence.setTied(statPositionPointsSequence.getTied() + 1);
					break;
				}
				case C: {
					statPositionPointsSequence.setVisitorWinner(statPositionPointsSequence.getVisitorWinner() + 1);
					break;
				}
			}
			
			statPositionPointsSequenceRepository.save(statPositionPointsSequence);
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
