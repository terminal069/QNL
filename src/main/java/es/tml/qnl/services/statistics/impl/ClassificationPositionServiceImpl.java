package es.tml.qnl.services.statistics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.PositionRequest;
import es.tml.qnl.model.mongo.League;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.SeasonData;
import es.tml.qnl.model.mongo.StatClassificationPosition;
import es.tml.qnl.repositories.mongo.LeagueRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.SeasonDataRepository;
import es.tml.qnl.repositories.mongo.StatClassificationPositionRepository;
import es.tml.qnl.services.statistics.ClassificationPositionService;
import es.tml.qnl.util.TimeLeftEstimator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClassificationPositionServiceImpl implements ClassificationPositionService {

	private static final String ROUND_NUMBER = "roundNumber";

	@Autowired
	private StatClassificationPositionRepository statClassificationPositionRepository;
	
	@Autowired
	private LeagueRepository leagueRepository;
	
	@Autowired
	private SeasonDataRepository seasonDataRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private TimeLeftEstimator timeLeftEstimator;
	
	private Map<String, Integer> previousPositions = new HashMap<>();
	
	private int minRound;
	
	@Override
	public void calculateClassificationPosition(PositionRequest request) {

		this.minRound = request.getMinRound();
		List<SeasonData> seasonData = seasonDataRepository.findAll();
		List<League> leagues = leagueRepository.findAll();
		int totalCombinations = seasonData.size() * leagues.size();
		timeLeftEstimator.init(totalCombinations);
		
		// Delete data from repository
		statClassificationPositionRepository.deleteAll();
		
		// Calculate statistics
		leagues.forEach(league -> {
			seasonData.forEach(season -> {
				log.debug("Calculating statistics for league '{}' and season '{}' from round '{}' - Estimated time left: {}",
						league.getCode(), season.getYear(), minRound, timeLeftEstimator.getTimeLeft());
				
				timeLeftEstimator.startPartial();
				previousPositions.clear();
				
				roundRepository.findByLeagueAndSeasonSorted(
						league.getCode(),
						season.getYear(),
						new Sort(ROUND_NUMBER))
					.forEach(round -> calculateResults(round));
				
				timeLeftEstimator.finishPartial();
			});
		});
	}

	private void calculateResults(Round round) {

		Integer localPreviousPosition = previousPositions.get(round.getLocal());
		Integer visitorPreviousPosition = previousPositions.get(round.getVisitor());
		
		if (round.getRoundNumber() >= minRound
				&& localPreviousPosition != null && visitorPreviousPosition != null) {
			
			int previousPosition = localPreviousPosition - visitorPreviousPosition;
			
			StatClassificationPosition statClassificationPosition = Optional.ofNullable(
					statClassificationPositionRepository.findByPositionDifference(previousPosition))
				.orElse(new StatClassificationPosition(previousPosition));
			
			if (round.getLocalRes() > round.getVisitorRes()) {
				statClassificationPosition.setLocalWinner(statClassificationPosition.getLocalWinner() + 1);
			}
			else if (round.getLocalRes() == round.getVisitorRes()) {
				statClassificationPosition.setTied(statClassificationPosition.getTied() + 1);
			}
			else {
				statClassificationPosition.setVisitorWinner(statClassificationPosition.getVisitorWinner() + 1);
			}
			
			statClassificationPositionRepository.save(statClassificationPosition);
		}
		
		previousPositions.put(round.getLocal(), round.getLocalPosition());
		previousPositions.put(round.getVisitor(), round.getVisitorPosition());
	}
}
