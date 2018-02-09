package es.tml.qnl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.model.mongo.GenericRound;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.services.statistics.util.StatisticsUtils;

/**
 * Component used to make predictions about the results of matches
 * 
 * @author jcerrato
 *
 */
@Component
public class Predictor {
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private StatisticsUtils statisticsUtils;

	/**
	 * Predict results from a list of matches
	 * 
	 * @param matches List of matches
	 * @return List of predictions
	 */
	public List<Prediction> predict(List<Match> matches) {

		List<Prediction> predictions = new ArrayList<>();
		
		matches.forEach(match -> predictions.add(predict(match)));
		
		return predictions;
	}

	/**
	 * Predict result from a match
	 * 
	 * @param match Match
	 * @return Prediction
	 */
	public Prediction predict(Match match) {

		Prediction prediction;
		
		// Get round
		GenericRound round = getRound(match);
		
		if (round != null) {
			prediction = calculatePrediction(round);
		}
		else {
			prediction = new Prediction(match.getLocal(), match.getVisitor());
		}
		
		return prediction;
	}

	/**
	 * Search for a round in the round and round prediction repositories from the data of a match
	 * 
	 * @param match Match data
	 * @return A round, or {@code null} if no round is found
	 */
	private GenericRound getRound(Match match) {
		
		GenericRound round = Optional.ofNullable(roundRepository.findByLeagueAndSeasonAndRoundAndLocalAndVisitor(
				match.getLeague(),
				match.getSeason(),
				match.getRound(),
				match.getLocal(),
				match.getVisitor()))
			.orElse(null);
		
		if (round == null) {
			round = Optional.ofNullable(roundPredictionRepository.findByLeagueAndSeasonAndRoundAndLocalAndVisitor(
					match.getLeague(),
					match.getSeason(),
					match.getRound(),
					match.getLocal(),
					match.getVisitor()))
				.orElse(null);
		}
		
		return round;
	}
	
	/**
	 * Calculates an estimation of the results of a match from the data of a round
	 * 
	 * @param round Round data
	 * @return A prediction of the results
	 */
	private Prediction calculatePrediction(GenericRound round) {
		
		// Get points, position and sequence from the match
		int points = statisticsUtils.getPointsBeforeMatch(round);
		int position = statisticsUtils.getPositionBeforeMatch(round);
		String sequence = getSequence(round);
		
		
		
		
		
		
		
		return null;
	}

	private String getSequence(GenericRound round) {
		// TODO Auto-generated method stub
		return null;
	}

}
