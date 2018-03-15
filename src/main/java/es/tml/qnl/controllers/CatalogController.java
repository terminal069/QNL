package es.tml.qnl.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(
		produces = { MediaType.APPLICATION_JSON_VALUE },
		path = "/qnl/catalog")
public class CatalogController {
	
	private static final String ROUND_NUMBER = "roundNumber";
	private static final String SEASON_CODE = "seasonCode";
	private static final String LEAGUE_CODE = "leagueCode";
	private static final String LOCAL = "local";
	private static final String VISITOR = "visitor";
	
	@Autowired
	private CatalogService catalogService;
	
	@PostMapping(
			value = "/seasons",
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(
			value = "Generate season data",
			notes = "This service is used to generate season data from data stored in repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly generated season data"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void generateSeasons() {
		
		catalogService.generateSeasons();
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value = HttpStatus.CREATED)
	@ApiOperation(
			value = "Load catalog data into repository",
			notes = "This service is used to load catalog data into the repository. Two behaviours are permited: "
					+ "if no league code is provided, it loads data from all leagues; otherwise, it only loads data "
					+ "from specified league")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly loaded catalog data"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
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
	@ApiOperation(
			value = "Get round data",
			notes = "This service is used to get data from a round based on request parameters")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = ROUND_NUMBER, value = "Round number", dataType = "number", paramType = "query"),
	    @ApiImplicitParam(name = SEASON_CODE, value = "Season code", dataType = "number", paramType = "query"),
	    @ApiImplicitParam(name = LEAGUE_CODE, value = "League code", dataType = "string", paramType = "query"),
	    @ApiImplicitParam(name = LOCAL, value = "Local team", dataType = "string", paramType = "query"),
	    @ApiImplicitParam(name = VISITOR, value = "Visitor team", dataType = "string", paramType = "query")
	  })
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_OK, message = "Properly gotten round data"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
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
	@ApiOperation(
			value = "Get all teams",
			notes = "This service is used to get data from all teams stored in the repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_OK, message = "Properly gotten team data"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public List<GetTeamsResponse> getTeams() {
		
		return catalogService.getTeams();
	}
	
}
