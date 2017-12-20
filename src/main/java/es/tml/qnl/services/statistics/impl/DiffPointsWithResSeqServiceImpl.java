package es.tml.qnl.services.statistics.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.enums.Result;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.StatDiffPointsResSeq;
import es.tml.qnl.model.mongo.Team;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.StatDiffPointsResSeqRepository;
import es.tml.qnl.repositories.mongo.TeamRepository;
import es.tml.qnl.services.statistics.DiffPointsWithResSeqService;
import es.tml.qnl.util.FIFOQueue;
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
	private StatDiffPointsResSeqRepository statDiffPointsResSeqRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private FIFOQueue<Result> fifoQueue;
	
	private List<Team> teams;
	
	@Override
	public void calculateDiffPointsWithResSeq(int maxIterations) {

		log.info("------------------- START (calculateDiffPointsWithResSeq) -------------------");
		
		// Delete all data from repository
		statDiffPointsResSeqRepository.deleteAll();
		
		// Get all teams
		teams = teamRepository.findAll();
		
		// Iterate and calculate results for each sequence
		IntStream.rangeClosed(1, maxIterations).forEach(iterationNumber -> {
			fifoQueue.clear();
			fifoQueue.setSize(iterationNumber);
			performIteration(iterationNumber);
		});
		
		log.info("-------------------  END (calculateDiffPointsWithResSeq)  -------------------");
	}
	
	private void performIteration(int iterationNumber) {
		
		log.info("Performing iteration with a sequence of {} elements", iterationNumber);
		
		teams.forEach(team -> {
			roundRepository.getRoundByTeam(team.getName(), new Sort(SEASON_CODE, ROUND_NUMBER)).forEach(round -> {
				Result result = calculateResult(team.getName(), round);
				calculateDiffAndSequence(round, iterationNumber, result);
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
	
	private void calculateDiffAndSequence(Round round, int iterationNumber, Result result) {
		
		if (fifoQueue.getQueueSize() == iterationNumber) {
			
			int localPoints = round.getLocalPoints();
			int visitorPoints = round.getVisitorPoints();
			int difference;
			String sequence = fifoQueue.toStringFromHeadToTail();
			
			// Get difference before match
			if (round.getLocalRes() - round.getVisitorRes() > 0) {
				difference = localPoints - visitorPoints - win;
			}
			else if (round.getLocalRes() == round.getVisitorRes()) {
				difference = localPoints - visitorPoints;
			}
			else {
				difference = localPoints - visitorPoints + win;
			}
			
			StatDiffPointsResSeq statDiffPointsResSeq = Optional
					.ofNullable(statDiffPointsResSeqRepository.findByDifferenceAndSequence(difference, sequence))
					.orElse(new StatDiffPointsResSeq(difference, sequence));
			
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

}
