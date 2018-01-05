package es.tml.qnl.services.statistics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.statistics.PositionRequest;
import es.tml.qnl.model.mongo.Round;
import es.tml.qnl.model.mongo.SeasonData;
import es.tml.qnl.model.mongo.StatClassificationPosition;
import es.tml.qnl.repositories.mongo.LeagueRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.SeasonDataRepository;
import es.tml.qnl.repositories.mongo.StatClassificationPositionRepository;
import es.tml.qnl.services.statistics.ClassificationPositionService;
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
	
	private Map<String, Integer> previousPositions = new HashMap<>();
	
	private int minRound;
	
	@Override
	public void calculateClassificationPosition(PositionRequest request) {

		// Assign minimum round
		this.minRound = request.getMinRound();
		
		// Delete data from repository
		statClassificationPositionRepository.deleteAll();
		
		// Calculate statistics
		List<SeasonData> seasonData = seasonDataRepository.findAll();
		
		leagueRepository.findAll().forEach(league -> {
			seasonData.forEach(season -> {
				log.debug("Calculating statistics for league '{}' and season '{}' from round '{}'",
						league.getCode(), season.getYear(), minRound);
				previousPositions.clear();
				roundRepository.findByLeagueAndSeasonSorted(
						league.getCode(),
						season.getYear(),
						new Sort(ROUND_NUMBER))
					.forEach(round -> calculateResults(round));
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
