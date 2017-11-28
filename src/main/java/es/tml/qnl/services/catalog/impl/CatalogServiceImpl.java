package es.tml.qnl.services.catalog.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
import es.tml.qnl.model.mongo.Season;
import es.tml.qnl.repositories.mongo.SeasonRepository;
import es.tml.qnl.services.catalog.CatalogDataParserService;
import es.tml.qnl.services.catalog.CatalogService;

@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private CatalogDataParserService catalogDataParserService;
	
	@Override
	public LoadDataResponse loadData(LoadDataRequest request) {
		
		LoadDataResponse response = new LoadDataResponse();
		
		// Set filter in request
		if (request.getFromSeasonCode() == null) {
			request.setFromSeasonCode(Integer.MIN_VALUE);
		}
		if (request.getToSeasonCode() == null) {
			request.setToSeasonCode(Integer.MAX_VALUE);
		}
		
		// Get league data from DB and filter it by season
		Optional.of(seasonRepository.findByLeague(request.getLeagueCode()))
			.orElse(Collections.emptyList())
			.stream()
			.filter(season -> 
				season.getCode() >= request.getFromSeasonCode() 
				&& season.getCode() <= request.getToSeasonCode())
			.collect(Collectors.toList())
			.forEach(season -> {
				processData(request.getLeagueCode(), season);
			});
		
		return response;
	}

	private void processData(String league, Season season) {
		
		// Parse data from league
		catalogDataParserService.parseDataFromUrl(league, season);
		
		// Save parsed data into DB
		
	}

}
