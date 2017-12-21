package es.tml.qnl.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.tml.qnl.beans.catalog.GetRoundRequest;
import es.tml.qnl.beans.catalog.GetRoundResponse;
import es.tml.qnl.beans.catalog.GetTeamsResponse;
import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.services.catalog.CatalogService;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = {MediaType.APPLICATION_JSON_VALUE},
		path = "/qnl/catalog")
public class CatalogController {
	
	private static final String ROUND_NUMBER = "roundNumber";
	private static final String SEASON_CODE = "seasonCode";
	private static final String LEAGUE_CODE = "leagueCode";
	private static final String LOCAL = "local";
	private static final String VISITOR = "visitor";
	
	@Autowired
	private CatalogService catalogService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public void loadData(@RequestBody @Valid LoadDataRequest request) {
		
		if (StringUtils.isEmpty(request.getLeagueCode())) {
			catalogService.loadAllData();
		}
		else {
			catalogService.loadData(request);
		}
	}
	
	@GetMapping(value = "/rounds")
	@ResponseStatus(value = HttpStatus.OK)
	public List<GetRoundResponse> getRounds(@RequestParam Map<String, String> requestParams) {
		
		return catalogService.getRound(new GetRoundRequest(
				requestParams.get(ROUND_NUMBER) == null ? null : Integer.parseInt(requestParams.get(ROUND_NUMBER)),
				requestParams.get(SEASON_CODE) == null ? null : Integer.parseInt(requestParams.get(SEASON_CODE)),
				requestParams.get(LEAGUE_CODE),
				requestParams.get(LOCAL),
				requestParams.get(VISITOR)));
	}
	
	@GetMapping(value = "/teams")
	@ResponseStatus(value = HttpStatus.OK)
	public List<GetTeamsResponse> getTeams() {
		
		return catalogService.getTeams();
	}
}
