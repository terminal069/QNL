package es.tml.qnl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.tml.qnl.services.statistics.StatisticsService;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = {MediaType.APPLICATION_JSON_VALUE},
		path = "/qnl/statistics")
public class StatisticsController {
	
	@Autowired
	private StatisticsService statisticsService;

	@PostMapping(value = "/aVsB")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void calculateAVsB() {
		
		statisticsService.calculateAVsB();
	}
	
	@PostMapping(value = "/resultSequence")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void resultSequence() {
		
	}
}
