package es.tml.qnl.services.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.tml.qnl.beans.catalog.GetRoundRequest;
import es.tml.qnl.beans.catalog.GetRoundResponse;
import es.tml.qnl.beans.catalog.GetTeamsResponse;
import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.model.mongo.Season;
import es.tml.qnl.model.mongo.SeasonData;
import es.tml.qnl.repositories.mongo.LeagueRepository;
import es.tml.qnl.repositories.mongo.RoundRepository;
import es.tml.qnl.repositories.mongo.SeasonDataRepository;
import es.tml.qnl.repositories.mongo.SeasonRepository;
import es.tml.qnl.repositories.mongo.TeamRepository;
import es.tml.qnl.services.catalog.CatalogService;
import es.tml.qnl.util.CatalogDataParser;

@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private LeagueRepository leagueRepository;
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private SeasonDataRepository seasonDataRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private CatalogDataParser catalogDataParser;
	
	@Override
	public void generateSeasons() {

		seasonRepository.deleteAll();
		
		List<SeasonData> seasonsData = seasonDataRepository.findAll();
		
		leagueRepository.findAll().forEach(league -> {
			seasonsData.forEach(seasonData -> {
				seasonRepository.save(new Season(
						seasonData.getYear(),
						seasonData.getSeasonRange(),
						league.getCode(),
						new StringBuilder()
							.append(league.getPrefix())
							.append(seasonData.getSeasonRange())
							.append(league.getSuffix())
							.toString()));
			});
		});
	}
	
	@Override
	public void loadData(LoadDataRequest request) {
		
		loadAndParseData(request);
	}
	
	@Override
	public void loadAllData() {
		
		leagueRepository.findAll().forEach(league -> {
			loadAndParseData(new LoadDataRequest(league.getCode()));
		});
	}
	
	private void loadAndParseData(LoadDataRequest request) {
		
		// Set filter in request
		if (request.getFromSeasonCode() == null) {
			request.setFromSeasonCode(Integer.MIN_VALUE);
		}
		if (request.getToSeasonCode() == null) {
			request.setToSeasonCode(Integer.MAX_VALUE);
		}
		
		// Get league data from DB and filter it by season
		Optional.of(seasonRepository.findByLeagueAndSeasonCodeRank(
				request.getLeagueCode(),
				request.getFromSeasonCode(),
				request.getToSeasonCode()))
			.orElse(Collections.emptyList())
			.stream()
			.forEach(season -> {
				catalogDataParser.parseDataFromUrl(request.getLeagueCode(), season);
			});
	}
	
	@Override
	public List<GetRoundResponse> getRound(GetRoundRequest request) {
		
		List<GetRoundResponse> response = new ArrayList<>();
		
		roundRepository.getRoundByRoundSeasonLeagueLocalVisitor(
				request.getRoundNumber(),
				request.getSeasonCode(),
				request.getLeagueCode(),
				request.getLocal(),
				request.getVisitor())
			.stream()
			.forEach(round -> {
				response.add(new GetRoundResponse(
						round.getRoundNumber(),
						round.getSeasonCode(),
						round.getLeagueCode(),
						round.getLocal(),
						round.getVisitor(),
						round.getLocalRes(),
						round.getVisitorRes(),
						round.getLocalPoints(),
						round.getVisitorPoints()));
			});
		
		return response;
	}

	@Override
	public List<GetTeamsResponse> getTeams() {
		
		List<GetTeamsResponse> response = new ArrayList<>();
		
		teamRepository.findAll(new Sort("name"))
			.stream()
			.forEach(team -> {
				response.add(new GetTeamsResponse(team.getName()));
			});
		
		return response;
	}
	
}
