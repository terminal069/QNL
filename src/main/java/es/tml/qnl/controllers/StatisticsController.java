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

import es.tml.qnl.beans.statistics.StatisticsRequest;
import es.tml.qnl.enums.StatisticsType;
import es.tml.qnl.services.statistics.StatisticsService;
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
	private StatisticsService statisticsService;
	
	@PostMapping(value = "/points")
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
	public void points() {
		
		statisticsService.calculateStatistics(null, StatisticsType.POINTS);
	}
	
	@PostMapping(value = "/position")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate statistics by position",
			notes = "This service is used to calculate statistics based on the classification position of the "
					+ "teams and save them into repository. If two teams have the same points in the same round, "
					+ "position asigned will be the same")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void position(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.POSITION);
	}

	@PostMapping(value = "/sequence")
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
	public void sequence(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.SEQUENCE);
	}
	
	@PostMapping(value = "/pointsPosition")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate difference of points and difference of position statistics",
			notes = "This service is used to calculate statistics based on the difference of points each "
					+ "team has and the classification position, and save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void pointsPosition(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.POINTS_POSITION);
	}
	
	@PostMapping(value = "/pointsSequence")
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
	public void pointsSequence(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.POINTS_SEQUENCE);
	}
	
	@PostMapping(value = "/positionSequence")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate statistics by position and result sequence",
			notes = "This service is used to calculate statistics based on the classification position and the "
					+ "result sequence of the teams, and save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void positionSequence(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.POSITION_SEQUENCE);
	}
	
	@PostMapping(value = "/pointsPositionSequence")
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(
			value = "Calculate statistics by position, difference of points and result sequence",
			notes = "This service is used to calculate statistics based on the classification position, the "
					+ "difference of points each team has and the result sequence of the teams, "
					+ "and save them into repository")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Properly calculated statistics"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public void pointsPositionSequence(@Valid @RequestBody StatisticsRequest request) {
		
		statisticsService.calculateStatistics(request, StatisticsType.POINTS_POSITION_SEQUENCE);
	}
}
