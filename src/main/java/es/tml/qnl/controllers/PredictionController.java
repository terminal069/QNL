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

import es.tml.qnl.beans.prediction.GetPredictionRequest;
import es.tml.qnl.beans.prediction.GetPredictionResponse;
import es.tml.qnl.services.prediction.PredictionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = { MediaType.APPLICATION_JSON_VALUE },
		path = "/qnl/prediction")
@Api(value = "/qnl/prediction")
public class PredictionController {
	
	@Autowired
	private PredictionService predictionService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(
			value = "Make a prediction",
			notes = "This service is used to make a prediction from the matches included in the request")
	@ApiResponses({
		@ApiResponse(code = HttpServletResponse.SC_OK, message = "Properly made prediction"),
		@ApiResponse(code = HttpServletResponse.SC_METHOD_NOT_ALLOWED, message = "Method not allowed"),
		@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal server error")
	})
	public GetPredictionResponse getPrediction(@RequestBody @Valid GetPredictionRequest request) {
		
		return predictionService.makePrediction(request);
	}
}
