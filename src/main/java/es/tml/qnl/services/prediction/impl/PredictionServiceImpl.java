package es.tml.qnl.services.prediction.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import es.tml.qnl.beans.prediction.GetPredictionRequest;
import es.tml.qnl.beans.prediction.GetPredictionResponse;
import es.tml.qnl.beans.prediction.Prediction;
import es.tml.qnl.services.prediction.PredictionService;

@Service
public class PredictionServiceImpl implements PredictionService {

	@Override
	public GetPredictionResponse makePrediction(GetPredictionRequest request) {
		
		// Get data of league, season and round from web
		
		// Make prediction with data collected
		
		// Return prediction
		GetPredictionResponse response = new GetPredictionResponse();
		response.setId(request.getId());
		return response;
	}

	private List<Prediction> getData(String league, int season, int round) {

		
		
		return null;
	}

}
