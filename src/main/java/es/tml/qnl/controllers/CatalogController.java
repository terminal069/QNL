package es.tml.qnl.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.services.catalog.CatalogService;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = {MediaType.APPLICATION_JSON_VALUE},
		path = "/qnl/catalog")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;

	@PostMapping(value = "/loadData")
	@ResponseStatus(value = HttpStatus.CREATED)
	public void loadData(@RequestBody @Valid LoadDataRequest request) {
		
		catalogService.loadData(request);
	}
	
	@GetMapping(value = "/getRound")
	@ResponseStatus(value = HttpStatus.OK)
	public List<GetRoundResponse> getRound(@RequestParam Map<String, String> requestParams) {
		
		return catalogService.getRound(new GetRoundRequest(
				requestParams.get("roundNumber") == null ? null : Integer.parseInt(requestParams.get("roundNumber")),
				requestParams.get("seasonCode") == null ? null : Integer.parseInt(requestParams.get("seasonCode")),
				requestParams.get("leagueCode"),
				requestParams.get("local"),
				requestParams.get("visitor")));
	}
}
