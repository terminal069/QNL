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

import es.tml.qnl.beans.prediction.GetPredictionRequest;
import es.tml.qnl.beans.prediction.GetPredictionResponse;
import es.tml.qnl.services.prediction.PredictionService;

@RestController
@RequestMapping(
		consumes = { MediaType.APPLICATION_JSON_VALUE },
		produces = { MediaType.APPLICATION_JSON_VALUE },
		path = "/qnl/prediction")
public class PredictionController {
	
	@Autowired
	private PredictionService predictionService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.OK)
	public GetPredictionResponse getPrediction(@RequestBody @Valid GetPredictionRequest request) {
		
		return predictionService.makePrediction(request);
	}
}
