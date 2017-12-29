package es.tml.qnl.services.prediction.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.prediction.GetPredictionRequest;
import es.tml.qnl.beans.prediction.GetPredictionResponse;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.repositories.mongo.SeasonRepository;
import es.tml.qnl.services.prediction.PredictionService;
import es.tml.qnl.util.CatalogDataParser;
import es.tml.qnl.util.Predictor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {

	private static final String PIPE = "|";
	
	@Autowired
	private CatalogDataParser catalogDataParser;
	
	@Autowired
	private Predictor predictor;
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Override
	public GetPredictionResponse makePrediction(GetPredictionRequest request) {
		
		// Load current data from web
		loadCurrentData(request);
		
		// Make prediction with data collected
		
		// Return prediction
		GetPredictionResponse response = new GetPredictionResponse();
		response.setId(request.getId());
		return response;
	}

	private void loadCurrentData(GetPredictionRequest request) {

		// Group data request by league, season and round
		List<String> groupedData = groupData(request);
		
		// With data grouped, parse round data from web
		groupedData.forEach(group -> parseData(group));
	}
	
	private List<String> groupData(GetPredictionRequest request) {
		
		List<String> groupedData = new ArrayList<>();
		
		request.getMatches()
			.stream()
			.map(match -> new StringBuilder()
					.append(match.getLeague())
					.append(PIPE)
					.append(match.getSeason())
					.append(PIPE)
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
	
	private void parseData(String group) {
		
		String[] groupSplitted = group.split(PIPE);
		String leagueCode = groupSplitted[0];
		String seasonCode = groupSplitted[1];
		String round = groupSplitted[2];
		
		catalogDataParser.parseCurrentDataFromUrl(
				leagueCode,
				seasonRepository.findByLeagueAndSeason(leagueCode, seasonCode),
				Integer.parseInt(round));
	}

}
