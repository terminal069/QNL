package es.tml.qnl.controllers;

import javax.servlet.http.HttpServletResponse;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = { MediaType.APPLICATION_JSON_VALUE },
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
	@ApiOperation(
			value = "Calculate A vs B statistics",
			notes = "This service is used to calculate statistics from encounters of two teams and save "
					+ "them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void aVsB() {
		
		aVsBService.calculateAVsB();
	}
	
	@PostMapping(value = "/resultSequence")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate result sequence statistics",
			notes = "This service is used to calculate statistics from the result sequence of all teams and "
					+ "save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void resultSequence(@Valid @RequestBody ResultSequenceRequest request) {
		
		resultSequenceService.calculateResultSequence(request.getMaxIterations());
	}
	
	@PostMapping(value = "/differenceOfPoints")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate difference of points statistics",
			notes = "This service is used to calculate statistics from the difference of points two teams have "
					+ "and save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void differenceOfPoints() {
		
		differenceOfPointsService.calculateDifferenceOfPoints();
	}
	
	@PostMapping(value = "/diffPointsResSeq")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate difference of points and result sequence statistics",
			notes = "This service is used to calculate statistics from the result sequence of all teams and "
					+ "the difference of points each team has and save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void differenceOfPointsWithResultSequence(@Valid @RequestBody ResultSequenceRequest request) {
		
		diffPointsWithResSeqService.calculateDiffPointsWithResSeq(request.getMaxIterations());
	}
}
