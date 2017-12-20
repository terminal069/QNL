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

import es.tml.qnl.beans.statistics.ResultSequenceRequest;
import es.tml.qnl.services.statistics.AVsBService;
import es.tml.qnl.services.statistics.DiffPointsWithResSeqService;
import es.tml.qnl.services.statistics.DifferenceOfPointsService;
import es.tml.qnl.services.statistics.ResultSequenceService;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = {MediaType.APPLICATION_JSON_VALUE},
		path = "/qnl/statistics")
public class StatisticsController {
	
	@Autowired
	private AVsBService aVsBService;
	
	@Autowired
	private ResultSequenceService resultSequenceService;
	
	@Autowired
	private DifferenceOfPointsService differenceOfPointsService;
	
	@Autowired
	private DiffPointsWithResSeqService diffPointsWithResSeqService;

	@PostMapping(value = "/aVsB")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void aVsB() {
		
		aVsBService.calculateAVsB();
	}
	
	@PostMapping(value = "/resultSequence")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void resultSequence(@Valid @RequestBody ResultSequenceRequest request) {
		
		resultSequenceService.calculateResultSequence(request.getMaxIterations());
	}
	
	@PostMapping(value = "/differenceOfPoints")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void differenceOfPoints() {
		
		differenceOfPointsService.calculateDifferenceOfPoints();
	}
	
	@PostMapping(value = "/diffPointsResSeq")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void differenceOfPointsWithResultSequence(@Valid @RequestBody ResultSequenceRequest request) {
		
		diffPointsWithResSeqService.calculateDiffPointsWithResSeq(request.getMaxIterations());
	}
}
