package es.tml.qnl.services.prediction.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.prediction.PredictionRequest;
import es.tml.qnl.beans.prediction.PredictionResponse;
import es.tml.qnl.beans.prediction.Match;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.repositories.mongo.RoundPredictionRepository;
import es.tml.qnl.repositories.mongo.SeasonRepository;
import es.tml.qnl.services.prediction.PredictionService;
import es.tml.qnl.util.CatalogDataParser;
import es.tml.qnl.util.Predictor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {

	private static final String COLON = ":";
	
	@Autowired
	private CatalogDataParser catalogDataParser;
	
	@Autowired
	private Predictor predictor;
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private RoundPredictionRepository roundPredictionRepository;
	
	@Override
	public PredictionResponse makePrediction(PredictionRequest request) {
		
		// Delete old data
		roundPredictionRepository.deleteAll();
		
		// Load current data from web
		loadCurrentData(request.getMatches());
		
		// Make prediction with data collected
		List<Prediction> predictions = predictor.predict(request.getMatches());
		
		// Return prediction
		return new PredictionResponse(
				request.getId(),
				predictions);
	}

	/**
	 * Loads data into the repository for each match of the list
	 * 
	 * @param matches List of matches
	 */
	private void loadCurrentData(List<Match> matches) {

		groupData(matches) // Groups data
			.forEach(group -> parseData(group)); // With data grouped, parses round data from web
	}
	
	/**
	 * Groups data by league, season and round with the pattern {@code league:season:round}
	 * 
	 * @param matches List of matches
	 * @return A list of grouped data
	 */
	private List<String> groupData(List<Match> matches) {
		
		List<String> groupedData = new ArrayList<>();
		
		matches.stream()
			.map(match -> new StringBuilder()
					.append(match.getLeague())
					.append(COLON)
					.append(match.getSeason())
					.append(COLON)
					.append(match.getRound())
					.toString())
			.forEach(group -> {
				if (!groupedData.contains(group)) {
					groupedData.add(group);
				}
			});
		
		log.debug("Found {} data groups", groupedData.size());
		
		return groupedData;
	}
	
	/**
	 * For a group of data with the pattern {@code league:season:round}, parses this data and
	 * loads it into the data base
	 * 
	 * @param group Group of data
	 */
	private void parseData(String group) {
		
		String[] groupSplitted = group.split(COLON);
		String leagueCode = groupSplitted[0];
		int seasonCode = Integer.parseInt(groupSplitted[1]);
		int round = Integer.parseInt(groupSplitted[2]);
		
		catalogDataParser.parsePartialDataFromUrl(
				leagueCode,
				seasonRepository.findByLeagueAndSeason(leagueCode, seasonCode),
				round);
	}

}
