package es.tml.qnl.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.tml.qnl.beans.catalog.LoadDataRequest;
import es.tml.qnl.beans.catalog.LoadDataResponse;
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
	public LoadDataResponse loadData(@RequestBody @Valid LoadDataRequest request) {
		
		return catalogService.loadData(request);
	}
}
