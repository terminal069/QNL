package es.tml.qnl.controllers;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;
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

import es.tml.qnl.beans.prediction.PredictionRequest;
import es.tml.qnl.beans.prediction.PredictionResponse;
import es.tml.qnl.beans.prediction.WeightResponse;
import es.tml.qnl.services.prediction.PredictionService;
import es.tml.qnl.services.prediction.WeightService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(
		produces = { MediaType.APPLICATION_JSON_VALUE },
		path = "/qnl/prediction")
public class PredictionController {
	
	@Autowired
	private PredictionService predictionService;
	
	@Autowired
	private WeightService weightService;

	@PostMapping()
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(
			value = "Make a prediction",
			notes = "This service is used to make a prediction from the matches included in the request")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_OK, message = "Properly made prediction"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public PredictionResponse prediction(@RequestBody @Valid PredictionRequest request) {
		
		return predictionService.makePrediction(request);
	}
	
	@GetMapping(value = "/weights")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(
			value = "Calculate weights for each statistic",
			notes = "This service is used to calculate the weights of each statistic, iterating with different "
					+ "weights each time, to obtain the most accurate value of each one")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_OK, message = "All weights calculated succesfully"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public WeightResponse calculateWeights(
			@ApiParam(value = "Value used to calculate weights")
			@RequestParam BigDecimal increment,
			@ApiParam(value = "Maximum number of iterations used to calculate the sequence of results")
			@RequestParam Integer maxIterations) {
		
		return weightService.calculateWeights(increment, maxIterations);
	}
}
